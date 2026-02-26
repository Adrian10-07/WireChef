package com.example.wirechef.features.order.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wirechef.features.order.domain.entities.OrderItem
import com.example.wirechef.features.order.domain.usecases.CreateOrderUseCase
import com.example.wirechef.features.product.domain.entities.Product
import com.example.wirechef.features.product.domain.usecases.GetProductsUseCase
import com.example.wirechef.features.user.domain.usecases.GetUserByIdUseCase
import com.example.wirechef.core.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaiterViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(WaiterState())
    val uiState: StateFlow<WaiterState> = _uiState.asStateFlow()

    init {
        loadWaiterData()
        loadProducts(_uiState.value.selectedCategory)
    }

    private fun loadWaiterData() {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            viewModelScope.launch {
                val result = getUserByIdUseCase(userId)
                result.onSuccess { user ->
                    _uiState.update { it.copy(waiterName = user.name) }
                }
            }
        }
    }

    fun selectCategory(category: String) {
        if (_uiState.value.selectedCategory == category) return
        _uiState.update { it.copy(selectedCategory = category) }
        loadProducts(category)
    }

    private fun loadProducts(category: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingProducts = true, productsError = null) }
            val result = getProductsUseCase(category)
            result.fold(
                onSuccess = { productList ->
                    _uiState.update { it.copy(isLoadingProducts = false, products = productList) }
                },
                onFailure = { exception ->
                    _uiState.update { it.copy(isLoadingProducts = false, productsError = exception.message ?: "Error al cargar el menú") }
                }
            )
        }
    }

    fun addToCart(product: Product) {
        _uiState.update { currentState ->
            val newCart = currentState.cart.toMutableList()
            val existingItemIndex = newCart.indexOfFirst { it.product.id == product.id }

            if (existingItemIndex != -1) {
                val item = newCart[existingItemIndex]
                newCart[existingItemIndex] = item.copy(quantity = item.quantity + 1)
            } else {
                newCart.add(CartItem(product = product))
            }
            currentState.copy(cart = newCart)
        }
    }

    fun removeFromCart(product: Product) {
        _uiState.update { currentState ->
            val newCart = currentState.cart.filterNot { it.product.id == product.id }
            currentState.copy(cart = newCart)
        }
    }

    fun updateCartItemNote(product: Product, newNote: String) {
        _uiState.update { currentState ->
            val newCart = currentState.cart.map {
                if (it.product.id == product.id) it.copy(note = newNote) else it
            }
            currentState.copy(cart = newCart)
        }
    }

    fun updateTableNumber(number: String) {
        if (number.all { it.isDigit() }) {
            _uiState.update { it.copy(tableNumberInput = number) }
        }
    }

    fun sendOrder() {
        val currentState = _uiState.value
        val currentCart = currentState.cart

        if (currentCart.isEmpty()) return
        val tableNum = currentState.tableNumberInput.toIntOrNull()
        if (tableNum == null || tableNum <= 0) {
            _uiState.update { it.copy(orderSendError = "Ingresa un número de mesa válido") }
            return
        }

        val currentUserId = sessionManager.getUserId()

        if (currentUserId == -1) {
            _uiState.update { it.copy(orderSendError = "Error crítico: Sesión perdida") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSendingOrder = true, orderSendError = null) }

            val domainItems = currentCart.map { cartItem ->
                OrderItem(
                    productId = cartItem.product.id,
                    quantity = cartItem.quantity,
                    notes = cartItem.note
                )
            }
            val result = createOrderUseCase(
                tableNumber = tableNum,
                waiterId = currentUserId,
                items = domainItems
            )

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isSendingOrder = false,
                            cart = emptyList(),
                            tableNumberInput = "",
                            orderSendSuccess = true
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isSendingOrder = false,
                            orderSendError = exception.message ?: "Error al enviar pedido"
                        )
                    }
                }
            )
        }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(orderSendSuccess = false) }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(orderSendError = null) }
    }

    fun logout() {
        sessionManager.clearSession()
        _uiState.update { it.copy(isLoggedOut = true) }
    }
}
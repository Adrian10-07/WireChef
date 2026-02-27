package com.example.wirechef.features.order.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wirechef.core.di.WireChefWebSocketListener
import com.example.wirechef.core.session.SessionManager
import com.example.wirechef.features.order.domain.entities.OrderItem
import com.example.wirechef.features.order.domain.usecases.CreateOrderUseCase
import com.example.wirechef.features.order.domain.usecases.GetOrdersUseCase
import com.example.wirechef.features.product.domain.entities.Product
import com.example.wirechef.features.product.domain.usecases.GetProductsUseCase
import com.example.wirechef.features.user.domain.usecases.GetUserByIdUseCase
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
    private val getOrdersUseCase: GetOrdersUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val sessionManager: SessionManager,
    private val webSocketListener: WireChefWebSocketListener
) : ViewModel() {

    private val _uiState = MutableStateFlow(WaiterState())
    val uiState: StateFlow<WaiterState> = _uiState.asStateFlow()

    init {
        loadWaiterData()
        loadProducts(_uiState.value.selectedCategory)
        loadMyOrders()
        connectToWebSocket()
        listenToWebSocketMessages()
    }

    private fun loadWaiterData() {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            viewModelScope.launch {
                val result = getUserByIdUseCase(userId)
                result.onSuccess { user ->
                    _uiState.update { it.copy(waiterName = user.name) }
                }.onFailure {
                    _uiState.update { it.copy(waiterName = "Mesero Desconocido") }
                }
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        webSocketListener.disconnect()
        _uiState.update { it.copy(isLoggedOut = true) }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
        if (tabIndex == 1) {
            loadMyOrders()
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadProducts(category)
    }

    private fun loadProducts(category: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingProducts = true, productsError = null) }
            val result = getProductsUseCase(category)
            result.onSuccess { productsList ->
                _uiState.update { it.copy(isLoadingProducts = false, products = productsList) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoadingProducts = false, productsError = error.message) }
            }
        }
    }

    fun addToCart(product: Product, note: String = "") {
        _uiState.update { currentState ->
            val existingItem = currentState.cart.find { it.product.id == product.id && it.note == note }
            val newCart = if (existingItem != null) {
                currentState.cart.map {
                    if (it == existingItem) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                currentState.cart + CartItem(product, 1, note)
            }
            currentState.copy(cart = newCart)
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        _uiState.update { currentState ->
            val newCart = currentState.cart.toMutableList()
            newCart.remove(cartItem)
            currentState.copy(cart = newCart)
        }
    }

    fun updateTableNumber(number: String) {
        if (number.all { it.isDigit() }) {
            _uiState.update { it.copy(tableNumberInput = number) }
        }
    }
    fun sendOrder() {
        val currentCart = _uiState.value.cart
        val tableNumStr = _uiState.value.tableNumberInput
        val currentUserId = sessionManager.getUserId()

        if (currentCart.isEmpty()) {
            _uiState.update { it.copy(orderSendError = "El carrito está vacío") }
            return
        }
        val tableNum = tableNumStr.toIntOrNull()
        if (tableNum == null || tableNum <= 0) {
            _uiState.update { it.copy(orderSendError = "Ingresa un número de mesa válido") }
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
                    loadMyOrders()
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

    //LÓGICA DE MIS PEDIDOS Y WEBSOCKETS
    private fun loadMyOrders() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingOrders = true) }
            val result = getOrdersUseCase()
            result.onSuccess { allOrders ->
                // Filtramos para ver solo las órdenes creadas por este mesero específico
                val waiterOrders = allOrders.filter { it.waiterId == userId }
                _uiState.update { it.copy(myOrders = waiterOrders, isLoadingOrders = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoadingOrders = false) }
            }
        }
    }

    private fun connectToWebSocket() {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            webSocketListener.connect(role = "waiter", userId = userId)
        }
    }

    private fun listenToWebSocketMessages() {
        viewModelScope.launch {
            webSocketListener.messages.collect { messageJson ->
                try {
                    // Si recibimos una actualización de estado de la cocina, recargamos la lista
                    if (messageJson.contains("order_status_update")) {
                        loadMyOrders()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketListener.disconnect()
    }
}
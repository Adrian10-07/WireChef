package com.example.wirechef.features.order.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wirechef.core.di.WireChefWebSocketListener
import com.example.wirechef.core.session.SessionManager
import com.example.wirechef.features.order.domain.usecases.GetOrdersUseCase
import com.example.wirechef.features.order.domain.usecases.UpdateOrderStatusUseCase
import com.example.wirechef.features.user.domain.usecases.GetUserByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ChefViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val sessionManager: SessionManager,
    private val webSocketListener: WireChefWebSocketListener
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChefState())
    val uiState: StateFlow<ChefState> = _uiState.asStateFlow()

    init {
        loadChefName()
        loadInitialOrders()
        connectToWebSocket()
        listenToWebSocketMessages()
    }

    private fun loadChefName() {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            viewModelScope.launch {
                val result = getUserByIdUseCase(userId)
                result.onSuccess { user ->
                    _uiState.update { it.copy(chefName = user.name) }
                }.onFailure {
                    _uiState.update { it.copy(chefName = "Cocinero") }
                }
            }
        }
    }

    private fun loadInitialOrders() {
        viewModelScope.launch {
            val pendingResult = getOrdersUseCase(status = "pending")
            val preparingResult = getOrdersUseCase(status = "preparing")

            val allOrders = mutableListOf<com.example.wirechef.features.order.domain.entities.Order>()
            pendingResult.onSuccess { allOrders.addAll(it) }
            preparingResult.onSuccess { allOrders.addAll(it) }

            if (pendingResult.isSuccess || preparingResult.isSuccess) {
                _uiState.update { it.copy(orders = allOrders, isLoading = false) }
            } else {
                pendingResult.onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
            }
        }
    }

    private fun connectToWebSocket() {
        val userId = sessionManager.getUserId()
        if (userId != -1) {
            webSocketListener.connect(role = "chef", userId = userId)
        }
    }

    private fun listenToWebSocketMessages() {
        viewModelScope.launch {
            webSocketListener.messages.collect { messageJson ->
                try {
                    val json = JSONObject(messageJson)
                    val event = json.optString("event")
                    if (event == "new_order" || event == "order_status_update" || messageJson.contains("new_order") || messageJson.contains("order_status_update")) {
                        loadInitialOrders()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun updateOrderStatus(orderId: Int, newStatus: String) {
        // Optimistic UI update — change state instantly before the API responds
        _uiState.update { state ->
            if (newStatus == "ready") {
                state.copy(orders = state.orders.filter { it.id != orderId })
            } else {
                state.copy(orders = state.orders.map { order ->
                    if (order.id == orderId) order.copy(status = newStatus) else order
                })
            }
        }

        // Then fire the API call
        viewModelScope.launch {
            val result = updateOrderStatusUseCase(orderId, newStatus)
            result.onFailure { e ->
                // If API fails, reload from server to revert
                loadInitialOrders()
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    fun logout() {
        sessionManager.clearSession()
        webSocketListener.disconnect()
        _uiState.update { it.copy(isLoggedOut = true) }
    }

    override fun onCleared() {
        super.onCleared()
        webSocketListener.disconnect()
    }
}
package com.example.wirechef.features.order.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wirechef.core.di.WireChefWebSocketListener
import com.example.wirechef.core.session.SessionManager
import com.example.wirechef.features.order.domain.usecases.GetOrdersUseCase
import com.example.wirechef.features.order.domain.usecases.UpdateOrderStatusUseCase
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
    private val sessionManager: SessionManager,
    private val webSocketListener: WireChefWebSocketListener
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChefState())
    val uiState: StateFlow<ChefState> = _uiState.asStateFlow()

    init {
        loadInitialOrders()
        connectToWebSocket()
        listenToWebSocketMessages()
    }

    private fun loadInitialOrders() {
        viewModelScope.launch {
            val result = getOrdersUseCase(status = "pending")
            result.onSuccess { orders ->
                _uiState.update { it.copy(orders = orders, isLoading = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
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
                    if (json.optString("event") == "new_order" || messageJson.contains("new_order")) {
                        loadInitialOrders()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun updateOrderStatus(orderId: Int, newStatus: String) {
        viewModelScope.launch {
            val result = updateOrderStatusUseCase(orderId, newStatus)
            result.onSuccess { updatedOrder ->
                val updatedList = _uiState.value.orders.filter { it.id != orderId }.toMutableList()
                if (newStatus != "ready") {
                    updatedList.add(updatedOrder)
                }
                _uiState.update { it.copy(orders = updatedList) }
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
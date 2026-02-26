package com.example.wirechef.features.order.domain.repositories

import com.example.wirechef.features.order.domain.entities.Order
import com.example.wirechef.features.order.domain.entities.OrderItem

interface OrderRepository {
    // Permite filtrar opcionalmente por estado o mesa
    suspend fun getOrders(status: String? = null, table: Int? = null): List<Order>
    suspend fun getOrderById(id: Int): Order
    suspend fun createOrder(tableNumber: Int, waiterId: Int, items: List<OrderItem>): Order
    // Endpoint específico para cambiar status
    suspend fun updateOrderStatus(id: Int, status: String): Order
}
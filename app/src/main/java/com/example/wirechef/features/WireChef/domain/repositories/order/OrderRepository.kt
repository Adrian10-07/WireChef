package com.example.wirechef.features.WireChef.domain.repositories.order

import com.example.wirechef.features.WireChef.domain.entities.order.Order
import com.example.wirechef.features.WireChef.domain.entities.order.OrderItem

interface OrderRepository {
    // Permite filtrar opcionalmente por estado o mesa
    suspend fun getOrders(status: String? = null, table: Int? = null): List<Order>
    suspend fun getOrderById(id: Int): Order
    suspend fun createOrder(tableNumber: Int, waiterId: Int, items: List<OrderItem>): Order
    // Endpoint específico para cambiar status
    suspend fun updateOrderStatus(id: Int, status: String): Order
}
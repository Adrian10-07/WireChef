package com.example.wirechef.features.order.data.repositories

import com.example.wirechef.features.order.domain.entities.Order
import com.example.wirechef.features.order.domain.entities.OrderItem
import com.example.wirechef.features.order.domain.repositories.OrderRepository
import com.example.wirechef.features.order.data.datasource.remote.api.OrderApi
import com.example.wirechef.features.order.data.datasource.remote.mapper.toDomain
import com.example.wirechef.features.order.data.datasource.remote.models.OrderItemRequestDto
import com.example.wirechef.features.order.data.datasource.remote.models.OrderRequestDto
import com.example.wirechef.features.order.data.datasource.remote.models.OrderStatusUpdateDto
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val api: OrderApi
) : OrderRepository {

    override suspend fun getOrders(status: String?, table: Int?): List<Order> {
        return api.getOrders(status, table).map { it.toDomain() }
    }

    override suspend fun getOrderById(id: Int): Order {
        return api.getOrderById(id).toDomain()
    }

    override suspend fun createOrder(tableNumber: Int, waiterId: Int, items: List<OrderItem>): Order {
        val requestItems = items.map {
            OrderItemRequestDto(it.productId, it.quantity, it.notes)
        }
        val request = OrderRequestDto(tableNumber, waiterId, requestItems)
        return api.createOrder(request).toDomain()
    }

    override suspend fun updateOrderStatus(id: Int, status: String): Order {
        val request = OrderStatusUpdateDto(status)
        return api.updateOrderStatus(id, request).toDomain()
    }
}
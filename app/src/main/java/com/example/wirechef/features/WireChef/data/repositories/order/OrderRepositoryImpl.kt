package com.example.wirechef.features.WireChef.data.repositories.order

import com.example.wirechef.features.WireChef.data.datasource.remote.api.order.OrderApi
import com.example.wirechef.features.WireChef.data.datasource.remote.mapper.order.toDomain
import com.example.wirechef.features.WireChef.data.datasource.remote.models.order.OrderItemRequestDto
import com.example.wirechef.features.WireChef.data.datasource.remote.models.order.OrderRequestDto
import com.example.wirechef.features.WireChef.data.datasource.remote.models.order.OrderStatusUpdateDto
import com.example.wirechef.features.WireChef.domain.entities.order.Order
import com.example.wirechef.features.WireChef.domain.entities.order.OrderItem
import com.example.wirechef.features.WireChef.domain.repositories.order.OrderRepository
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
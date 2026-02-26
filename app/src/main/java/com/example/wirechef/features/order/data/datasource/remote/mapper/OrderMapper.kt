package com.example.wirechef.features.order.data.datasource.remote.mapper

import com.example.wirechef.features.order.data.datasource.remote.models.OrderDto
import com.example.wirechef.features.order.data.datasource.remote.models.OrderItemDto
import com.example.wirechef.features.order.domain.entities.Order
import com.example.wirechef.features.order.domain.entities.OrderItem

fun OrderItemDto.toDomain(): OrderItem {
    return OrderItem(
        productId = this.productId,
        quantity = this.quantity,
        notes = this.notes ?: ""
    )
}

fun OrderDto.toDomain(): Order {
    return Order(
        id = this.id,
        tableNumber = this.tableNumber,
        waiterId = this.waiterId,
        status = this.status,
        items = this.items?.map { it.toDomain() } ?: emptyList()
    )
}
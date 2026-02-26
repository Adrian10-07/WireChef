package com.example.wirechef.features.WireChef.data.datasource.remote.mapper.order

import com.example.wirechef.features.WireChef.data.datasource.remote.models.order.OrderDto
import com.example.wirechef.features.WireChef.data.datasource.remote.models.order.OrderItemDto
import com.example.wirechef.features.WireChef.domain.entities.order.Order
import com.example.wirechef.features.WireChef.domain.entities.order.OrderItem

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
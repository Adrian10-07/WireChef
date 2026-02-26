package com.example.wirechef.features.WireChef.domain.entities.order

data class Order(
    val id: Int,
    val tableNumber: Int,
    val waiterId: Int,
    val status: String, // "pending", "preparing", "ready", "delivered"
    val items: List<OrderItem>
)

package com.example.wirechef.features.order.domain.entities

data class OrderItem(
    val productId: Int,
    val quantity: Int,
    val notes: String
)
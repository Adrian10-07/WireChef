package com.example.wirechef.features.WireChef.domain.entities.order

data class OrderItem(
    val productId: Int,
    val quantity: Int,
    val notes: String
)
package com.example.wirechef.features.order.data.datasource.remote.models

import com.google.gson.annotations.SerializedName

data class OrderDto(
    val id: Int,
    @SerializedName("table_number") val tableNumber: Int,
    @SerializedName("waiter_id") val waiterId: Int,
    val status: String,
    val items: List<OrderItemDto>?
)

data class OrderItemDto(
    @SerializedName("product_id") val productId: Int,
    val quantity: Int,
    val notes: String?
)
package com.example.wirechef.features.order.data.datasource.remote.models

import com.google.gson.annotations.SerializedName

data class OrderRequestDto(
    @SerializedName("table_number") val tableNumber: Int,
    @SerializedName("waiter_id") val waiterId: Int,
    val items: List<OrderItemRequestDto>
)

data class OrderItemRequestDto(
    @SerializedName("product_id") val productId: Int,
    val quantity: Int,
    val notes: String
)
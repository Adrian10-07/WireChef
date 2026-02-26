package com.example.wirechef.features.product.data.datasource.remote.models

data class ProductRequestDto(
    val name: String,
    val description: String,
    val price: Double,
    val category: String
)
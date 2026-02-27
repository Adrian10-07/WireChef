package com.example.wirechef.features.product.data.datasource.remote.models

data class ProductDto(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: String
)
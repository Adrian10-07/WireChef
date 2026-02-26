package com.example.wirechef.features.WireChef.data.datasource.remote.models.product

data class ProductDto(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: String
)
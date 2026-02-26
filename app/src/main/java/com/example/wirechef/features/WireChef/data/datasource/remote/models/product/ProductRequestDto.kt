package com.example.wirechef.features.WireChef.data.datasource.remote.models.product

data class ProductRequestDto(
    val name: String,
    val description: String,
    val price: Double,
    val category: String
)
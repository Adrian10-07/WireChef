package com.example.wirechef.features.WireChef.domain.entities.product

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val category: String
)
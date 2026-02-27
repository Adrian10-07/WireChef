package com.example.wirechef.features.product.data.datasource.remote.mapper

import com.example.wirechef.features.product.data.datasource.remote.models.ProductDto
import com.example.wirechef.features.product.domain.entities.Product

fun ProductDto.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        category = this.category
    )
}
package com.example.wirechef.features.WireChef.data.datasource.remote.mapper.product

import com.example.wirechef.features.WireChef.data.datasource.remote.models.product.ProductDto
import com.example.wirechef.features.WireChef.domain.entities.product.Product

fun ProductDto.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        description = this.description,
        price = this.price,
        category = this.category
    )
}
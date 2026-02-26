package com.example.wirechef.features.product.domain.repositories

import com.example.wirechef.features.product.domain.entities.Product

interface ProductRepository {
    suspend fun getProducts(category: String? = null): List<Product>
    suspend fun getProductById(id: Int): Product
    suspend fun createProduct(name: String, description: String, price: Double, category: String): Product
    suspend fun updateProduct(id: Int, name: String, description: String, price: Double, category: String): Product
    suspend fun deleteProduct(id: Int)
}
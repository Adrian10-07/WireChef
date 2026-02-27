package com.example.wirechef.features.product.data.repositories

import android.util.Log
import com.example.wirechef.features.product.domain.entities.Product
import com.example.wirechef.features.product.domain.repositories.ProductRepository
import com.example.wirechef.features.product.data.datasource.remote.api.ProductApi
import com.example.wirechef.features.product.data.datasource.remote.mapper.toDomain
import com.example.wirechef.features.product.data.datasource.remote.models.ProductRequestDto
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi
) : ProductRepository {

    override suspend fun getProducts(category: String?): List<Product> {
        val response = api.getProducts(category)
        Log.d("ProductApi", "getProducts (category=$category): $response")
        return response.map { it.toDomain() }
    }

    override suspend fun getProductById(id: Int): Product {
        return api.getProductById(id).toDomain()
    }

    override suspend fun createProduct(name: String, description: String, price: Double, category: String): Product {
        val request = ProductRequestDto(name, description, price, category)
        return api.createProduct(request).toDomain()
    }

    override suspend fun updateProduct(id: Int, name: String, description: String, price: Double, category: String): Product {
        val request = ProductRequestDto(name, description, price, category)
        return api.updateProduct(id, request).toDomain()
    }

    override suspend fun deleteProduct(id: Int) {
        api.deleteProduct(id)
        Log.d("ProductApi", "deleteProduct: Deleted id $id")
    }
}
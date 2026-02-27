package com.example.wirechef.features.product.domain.usecases

import com.example.wirechef.features.product.domain.entities.Product
import com.example.wirechef.features.product.domain.repositories.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(category: String? = null): Result<List<Product>> {
        return try {
            val products = repository.getProducts(category)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
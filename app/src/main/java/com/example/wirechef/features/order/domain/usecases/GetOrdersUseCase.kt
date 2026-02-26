package com.example.wirechef.features.order.domain.usecases

import com.example.wirechef.features.order.domain.entities.Order
import com.example.wirechef.features.order.domain.repositories.OrderRepository
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(status: String? = null, table: Int? = null): Result<List<Order>> {
        return try {
            val orders = repository.getOrders(status, table)
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
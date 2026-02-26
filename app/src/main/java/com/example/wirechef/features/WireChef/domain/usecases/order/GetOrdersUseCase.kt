package com.example.wirechef.features.WireChef.domain.usecases.order

import com.example.wirechef.features.WireChef.domain.entities.order.Order
import com.example.wirechef.features.WireChef.domain.repositories.order.OrderRepository
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
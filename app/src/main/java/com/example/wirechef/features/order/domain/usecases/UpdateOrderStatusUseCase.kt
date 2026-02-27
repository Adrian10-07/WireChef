package com.example.wirechef.features.order.domain.usecases

import com.example.wirechef.features.order.domain.entities.Order
import com.example.wirechef.features.order.domain.repositories.OrderRepository
import javax.inject.Inject

class UpdateOrderStatusUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(id: Int, status: String): Result<Order> {
        return try {
            val validStatuses = listOf("pending", "preparing", "ready", "delivered")
            if (status !in validStatuses) {
                return Result.failure(Exception("Estado de orden inválido"))
            }
            val updatedOrder = repository.updateOrderStatus(id, status)
            Result.success(updatedOrder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
package com.example.wirechef.features.order.domain.usecases

import com.example.wirechef.features.order.domain.entities.Order
import com.example.wirechef.features.order.domain.entities.OrderItem
import com.example.wirechef.features.order.domain.repositories.OrderRepository
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(tableNumber: Int, waiterId: Int, items: List<OrderItem>): Result<Order> {
        return try {
            // Regla de negocio vista en el test_api: una orden no puede estar vacía
            if (items.isEmpty()) {
                return Result.failure(Exception("La orden debe tener al menos un producto"))
            }
            if (tableNumber <= 0) {
                return Result.failure(Exception("El número de mesa es inválido"))
            }

            val order = repository.createOrder(tableNumber, waiterId, items)
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
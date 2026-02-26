package com.example.wirechef.features.order.presentation.viewmodels

import com.example.wirechef.features.product.domain.entities.Product

data class CartItem(
    val product: Product,
    val quantity: Int = 1,
    val note: String = ""
)

data class WaiterState(
    val waiterName: String = "Cargando...",
    val isLoggedOut: Boolean = false,
    val isLoadingProducts: Boolean = false,
    val productsError: String? = null,
    val products: List<Product> = emptyList(),
    // Filtros de la pantalla
    val selectedCategory: String = "comidas",
    val tableNumberInput: String = "",

    val cart: List<CartItem> = emptyList(),
    val isSendingOrder: Boolean = false,
    val orderSendError: String? = null,
    val orderSendSuccess: Boolean = false
) {
    // Propiedad calculada: La vista solo leerá "cartTotal" y ya tendrá el dinero exacto
    val cartTotal: Double
        get() = cart.sumOf { it.product.price * it.quantity }
}
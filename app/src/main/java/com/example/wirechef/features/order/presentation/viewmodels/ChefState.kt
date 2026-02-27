package com.example.wirechef.features.order.presentation.viewmodels

import com.example.wirechef.features.order.domain.entities.Order

data class ChefState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isLoggedOut: Boolean = false
)
package com.example.wirechef.features.user.presentation.viewmodels

import com.example.wirechef.features.user.domain.entities.User

data class LoginState (
    val nameInput: String = "",
    val isLoading: Boolean = false,
    val error: String? =null,
    val loggedUser: User? = null,
    val welcomeMessage: String? = null
)
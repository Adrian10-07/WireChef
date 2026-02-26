package com.example.wirechef.features.user.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wirechef.core.session.SessionManager
import com.example.wirechef.features.user.domain.entities.User
import com.example.wirechef.features.user.domain.usecases.CreateUserUseCase
import com.example.wirechef.features.user.domain.usecases.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    fun onNameChanged(newName: String) {
        _uiState.update { it.copy(nameInput = newName, error = null) }
    }

    fun login(role: String) {
        val currentName = _uiState.value.nameInput.trim()

        if (currentName.isBlank()) {
            _uiState.update { it.copy(error = "Por favor, ingresa tu nombre") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val usersResult = getUsersUseCase()
            var existingUser: User? = null

            if (usersResult.isSuccess) {
                existingUser = usersResult.getOrNull()?.find { it.name.equals(currentName, ignoreCase = true) }
            }

            if (existingUser != null) {
                sessionManager.saveUserId(existingUser!!.id)
                val rolTraducido = if (existingUser!!.role == "waiter") "Mesero" else "Cocinero"

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loggedUser = existingUser,
                        welcomeMessage = "¡Bienvenido de nuevo $rolTraducido: ${existingUser!!.name}!"
                    )
                }
            } else {
                val createResult = createUserUseCase(currentName, role)

                createResult.fold(
                    onSuccess = { newUser ->
                        sessionManager.saveUserId(newUser.id)
                        val rolTraducido = if (newUser.role == "waiter") "Mesero" else "Cocinero"
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loggedUser = newUser,
                                welcomeMessage = "¡Usuario creado! Bienvenido $rolTraducido: ${newUser.name}"
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update { it.copy(isLoading = false, error = exception.message ?: "Error desconocido") }
                    }
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
package com.example.wirechef.features.user.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wirechef.features.user.domain.usecases.CreateUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    fun onNameChanged(newName: String) {
        _uiState.update { it.copy(nameInput = newName, error = null) }
    }

    fun login(role: String){
        val currentName = _uiState.value.nameInput

        if (currentName.isBlank()) {
            _uiState.update { it.copy(error = "Por favor, ingresa tu nombre") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = createUserUseCase(currentName, role)

            result.fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(isLoading = false, loggedUser = user) }
                },
                onFailure = { exception ->
                    _uiState.update { it.copy(isLoading = false, error = exception.message ?: "Error desconocido") }
                }
            )
        }
    }
    fun clearError(){
        _uiState.update { it.copy(error = null) }
    }
}
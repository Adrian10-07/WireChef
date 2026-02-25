package com.example.wirechef.features.WireChef.domain.usecases.user
import com.example.wirechef.features.WireChef.domain.entities.user.User
import com.example.wirechef.features.WireChef.domain.repositories.user.UserRepository
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(name: String, role: String): Result<User> {
        return try {
            // Validación básica
            if (name.isBlank() || role.isBlank()) {
                return Result.failure(Exception("El nombre y el rol son obligatorios"))
            }
            val newUser = repository.createUser(name, role)
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
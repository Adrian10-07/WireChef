package com.example.wirechef.features.WireChef.domain.usecases.user

import com.example.wirechef.features.WireChef.domain.entities.user.User
import com.example.wirechef.features.WireChef.domain.repositories.user.UserRepository
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<List<User>> {
        return try {
            val users = repository.getUsers()
            if (users.isEmpty()) {
                Result.failure(Exception("No se encontraron usuarios"))
            } else {
                Result.success(users)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
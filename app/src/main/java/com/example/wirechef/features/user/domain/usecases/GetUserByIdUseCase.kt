package com.example.wirechef.features.user.domain.usecases

import com.example.wirechef.features.user.domain.entities.User
import com.example.wirechef.features.user.domain.repositories.UserRepository
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(id: Int): Result<User> {
        return try {
            val user = repository.getUserById(id)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
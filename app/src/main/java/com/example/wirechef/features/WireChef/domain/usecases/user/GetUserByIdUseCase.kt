package com.example.wirechef.features.WireChef.domain.usecases.user

import com.example.wirechef.features.WireChef.domain.entities.user.User
import com.example.wirechef.features.WireChef.domain.repositories.user.UserRepository
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
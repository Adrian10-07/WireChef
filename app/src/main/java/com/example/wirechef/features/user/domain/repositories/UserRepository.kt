package com.example.wirechef.features.user.domain.repositories

import com.example.wirechef.features.user.domain.entities.User

interface UserRepository {
    suspend fun getUsers(): List<User>
    suspend fun getUserById(id: Int): User
    suspend fun createUser(name: String, role: String): User
}
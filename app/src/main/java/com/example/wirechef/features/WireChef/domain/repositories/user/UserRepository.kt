package com.example.wirechef.features.WireChef.domain.repositories.user
import com.example.wirechef.features.WireChef.domain.entities.user.User
interface UserRepository {
    suspend fun getUsers(): List<User>
    suspend fun getUserById(id: Int): User
    suspend fun createUser(name: String, role: String): User
}
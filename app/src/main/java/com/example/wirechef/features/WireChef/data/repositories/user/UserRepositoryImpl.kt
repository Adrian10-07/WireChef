package com.example.wirechef.features.WireChef.data.repositories.user
import com.example.wirechef.features.WireChef.data.datasource.remote.mapper.user.toDomain
import com.example.wirechef.features.WireChef.domain.entities.User
import com.example.wirechef.features.WireChef.domain.repositories.user.UserRepository
import com.example.wirechef.features.WireChef.data.datasource.remote.api.user.UserApi
import android.util.Log
import javax.inject.Inject

class UserRepositoryImpl@Inject constructor(
    private val api: UserApi
) : UserRepository {

    override suspend fun getUsers(): List<User> {
        val response = api.getUsers()
        Log.d("UserApi", "getUsers: $response")
        return response.map { it.toDomain() }
    }

    override suspend fun getUserById(id: Int): User {
        val response = api.getUserById(id)
        Log.d("UserApi", "getUserById: $response")
        return response.toDomain()
    }

    override suspend fun createUser(name: String, role: String): User {
        val request = UserRequestDto(name = name, role = role)
        val response = api.createUser(request)
        Log.d("UserApi", "createUser: $response")
        return response.toDomain()
    }
}
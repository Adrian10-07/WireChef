package com.example.wirechef.features.user.data.datasource.remote.api

import com.example.wirechef.features.user.data.datasource.remote.models.UserDto
import com.example.wirechef.features.user.data.datasource.remote.models.UserRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {
    @GET("api/users")
    suspend fun getUsers(): List<UserDto>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserDto

    @POST("api/users")
    suspend fun createUser(@Body request: UserRequestDto): UserDto
}
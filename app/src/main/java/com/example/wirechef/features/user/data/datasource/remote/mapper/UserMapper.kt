package com.example.wirechef.features.user.data.datasource.remote.mapper

import com.example.wirechef.features.user.data.datasource.remote.models.UserDto
import com.example.wirechef.features.user.domain.entities.User

fun UserDto.toDomain(): User {
    return User(
        id = this.id,
        name = this.name,
        role = this.role
    )
}
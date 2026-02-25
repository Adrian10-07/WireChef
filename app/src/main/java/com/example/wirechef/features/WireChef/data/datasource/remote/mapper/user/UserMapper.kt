package com.example.wirechef.features.WireChef.data.datasource.remote.mapper.user

import com.example.wirechef.features.WireChef.data.datasource.remote.models.user.UserDto
import com.example.wirechef.features.WireChef.domain.entities.user.User

fun UserDto.toDomain(): User {
    return User(
        id = this.id,
        name = this.name,
        role = this.role
    )
}
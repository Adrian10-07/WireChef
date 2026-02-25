package com.example.wirechef.features.WireChef.data.di.user

import com.example.wirechef.features.WireChef.data.repositories.user.UserRepositoryImpl
import com.example.wirechef.features.WireChef.domain.repositories.user.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UserRepositoryModule {
    @Binds
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}
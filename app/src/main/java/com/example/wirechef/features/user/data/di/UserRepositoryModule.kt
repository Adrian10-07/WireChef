package com.example.wirechef.features.user.data.di

import com.example.wirechef.features.user.data.repositories.UserRepositoryImpl
import com.example.wirechef.features.user.domain.repositories.UserRepository
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
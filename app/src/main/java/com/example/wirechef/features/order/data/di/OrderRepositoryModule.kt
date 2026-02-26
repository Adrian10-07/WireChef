package com.example.wirechef.features.order.data.di

import com.example.wirechef.features.order.data.repositories.OrderRepositoryImpl
import com.example.wirechef.features.order.domain.repositories.OrderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class OrderRepositoryModule {
    @Binds
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository
}
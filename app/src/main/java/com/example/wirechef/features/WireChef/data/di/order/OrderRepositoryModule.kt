package com.example.wirechef.features.WireChef.data.di.order

import com.example.wirechef.features.WireChef.data.repositories.order.OrderRepositoryImpl
import com.example.wirechef.features.WireChef.domain.repositories.order.OrderRepository
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
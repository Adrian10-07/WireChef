package com.example.wirechef.features.product.data.di

import com.example.wirechef.features.product.data.repositories.ProductRepositoryImpl
import com.example.wirechef.features.product.domain.repositories.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductRepositoryModule {
    @Binds
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository
}
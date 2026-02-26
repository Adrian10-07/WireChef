package com.example.wirechef.features.WireChef.data.di.product

import com.example.wirechef.features.WireChef.data.repositories.product.ProductRepositoryImpl
import com.example.wirechef.features.WireChef.domain.repositories.product.ProductRepository
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
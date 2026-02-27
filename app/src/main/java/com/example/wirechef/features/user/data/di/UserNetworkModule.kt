package com.example.wirechef.features.user.data.di
import com.example.wirechef.features.user.data.datasource.remote.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object UserNetworkModule {
    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }
}
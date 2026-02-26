package com.example.wirechef.features.product.data.datasource.remote.api

import com.example.wirechef.features.product.data.datasource.remote.models.ProductDto
import com.example.wirechef.features.product.data.datasource.remote.models.ProductRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {
    @GET("api/products")
    suspend fun getProducts(@Query("category") category: String?): List<ProductDto>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductDto

    @POST("api/products")
    suspend fun createProduct(@Body request: ProductRequestDto): ProductDto

    @PUT("api/products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body request: ProductRequestDto): ProductDto

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int)
}
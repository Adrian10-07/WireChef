package com.example.wirechef.features.WireChef.data.datasource.remote.api.order

import com.example.wirechef.features.WireChef.data.datasource.remote.models.order.OrderDto
import com.example.wirechef.features.WireChef.data.datasource.remote.models.order.OrderRequestDto
import com.example.wirechef.features.WireChef.data.datasource.remote.models.order.OrderStatusUpdateDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApi {
    @GET("api/orders")
    suspend fun getOrders(
        @Query("status") status: String?,
        @Query("table") table: Int?
    ): List<OrderDto>

    @GET("api/orders/{id}")
    suspend fun getOrderById(@Path("id") id: Int): OrderDto

    @POST("api/orders")
    suspend fun createOrder(@Body request: OrderRequestDto): OrderDto

    @PUT("api/orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") id: Int,
        @Body request: OrderStatusUpdateDto
    ): OrderDto
}
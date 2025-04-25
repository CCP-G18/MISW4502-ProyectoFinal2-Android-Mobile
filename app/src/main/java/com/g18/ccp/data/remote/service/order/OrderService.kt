package com.g18.ccp.data.remote.service.order

import com.g18.ccp.data.remote.model.order.Order
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OrderService {
    @GET("orders")
    suspend fun getOrders(): List<Order>

    @POST("order")
    suspend fun createOrder(@Body order: Order): Response<Order>
}

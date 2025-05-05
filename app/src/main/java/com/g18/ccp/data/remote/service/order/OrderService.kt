package com.g18.ccp.data.remote.service.order

import com.g18.ccp.data.remote.model.order.Order
import com.g18.ccp.data.remote.model.order.OrdersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OrderService {
    @GET("orders/customer")
    suspend fun getOrders(): OrdersResponse

    @POST("orders")
    suspend fun createOrder(@Body order: Order): Response<Order>
}

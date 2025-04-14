package com.g18.ccp.data.remote.service.order

import com.g18.ccp.data.remote.model.order.Order
import retrofit2.http.GET

interface OrderService {
    @GET("orders")
    suspend fun getOrders(): List<Order>
}

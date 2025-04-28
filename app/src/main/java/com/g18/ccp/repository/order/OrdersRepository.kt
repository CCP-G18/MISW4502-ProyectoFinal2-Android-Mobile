package com.g18.ccp.repository.order

import com.g18.ccp.data.remote.model.order.Order
import retrofit2.Response

interface OrdersRepository {
    suspend fun getOrders(): List<Order>
    suspend fun createOrder(order: Order): Response<Order>
}

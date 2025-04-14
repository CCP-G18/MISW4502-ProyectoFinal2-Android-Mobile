package com.g18.ccp.repository.order

import com.g18.ccp.data.remote.model.order.Order

interface OrdersRepository {
    suspend fun getOrders(): List<Order>
}

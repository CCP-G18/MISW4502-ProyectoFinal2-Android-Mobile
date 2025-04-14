package com.g18.ccp.repository.order

import com.g18.ccp.data.remote.model.order.Order
import com.g18.ccp.data.remote.service.order.OrderService

class OrdersRepositoryImpl(private val orderService: OrderService): OrdersRepository {
    override suspend fun getOrders(): List<Order> = orderService.getOrders()
}

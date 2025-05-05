package com.g18.ccp.data.remote.model.order

data class OrdersResponse(
    val code: Int,
    val data: List<Order>,
)

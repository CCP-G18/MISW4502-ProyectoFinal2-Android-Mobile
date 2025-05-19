package com.g18.ccp.data.remote.model.seller.order.product.order

import com.google.gson.annotations.SerializedName

data class SellerOrderRequest(
    val date: String,
    val total: Double,
    @SerializedName("customer_id")
    val customerId: String,
    val items: List<SellerOrderItem>
)

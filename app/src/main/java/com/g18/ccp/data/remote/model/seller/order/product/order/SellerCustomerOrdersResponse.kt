package com.g18.ccp.data.remote.model.seller.order.product.order

import com.google.gson.annotations.SerializedName

data class SellerCustomerOrdersResponse(
    val code: Int,
    val data: List<SellerCustomerOrderData>,
    val message: String,
    val status: String
)

data class SellerCustomerOrderData(
    val date: String,
    val items: List<SellerCustomerOrderItem>,
    @SerializedName("order_id")
    val orderId: String,
    val status: String,
    val summary: String,
    val total: Double
)

data class SellerCustomerOrderItem(
    @SerializedName("image_url") val imageUrl: String,
    val price: Double,
    val quantity: Int,
    val title: String,
    val description: String? = null
)

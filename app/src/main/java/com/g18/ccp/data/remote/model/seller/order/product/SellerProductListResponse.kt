package com.g18.ccp.data.remote.model.seller.order.product

data class SellerProductListResponse(
    val code: Int,
    val data: List<SellerProductData>,
    val message: String,
    val status: String
)

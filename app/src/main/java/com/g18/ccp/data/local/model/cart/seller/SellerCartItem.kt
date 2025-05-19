package com.g18.ccp.data.local.model.cart.seller

import com.g18.ccp.data.remote.model.seller.order.product.SellerProductData

data class SellerCartItem(
    val quantity: Int = 1,
    val product: SellerProductData
)


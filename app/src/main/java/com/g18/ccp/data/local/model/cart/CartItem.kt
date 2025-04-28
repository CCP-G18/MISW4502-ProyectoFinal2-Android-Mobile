package com.g18.ccp.data.local.model.cart

import com.g18.ccp.data.remote.model.product.Product

data class CartItem(
    var quantity: Int = 1,
    val product: Product
)

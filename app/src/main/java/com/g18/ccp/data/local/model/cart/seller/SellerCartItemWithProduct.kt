package com.g18.ccp.data.local.model.cart.seller

data class SellerCartItemWithProduct(
    val productId: String,
    val quantityInCart: Int,
    val name: String,
    val description: String,
    val price: Float,
    val imageUrl: String?,
    val stock: Int
)

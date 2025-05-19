package com.g18.ccp.data.local.model.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seller_cart_items_v2")
data class SellerCartItemEntity(
    @PrimaryKey
    val productId: String,
    var quantityInCart: Int
)

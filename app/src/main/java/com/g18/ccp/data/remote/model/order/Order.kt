package com.g18.ccp.data.remote.model.order

import android.os.Parcelable
import com.g18.ccp.core.constants.enums.OrderStatus
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: String,
    val summary: String,
    val date: String,
    val total: Float,
    val status: OrderStatus,
    val items: List<OrderItem> = emptyList()
): Parcelable

@Parcelize
data class OrderItem(
    val title: String,
    val quantity: Int,
    val price: Float,
    @SerializedName("image_url")
    val imageUrl: String,
): Parcelable

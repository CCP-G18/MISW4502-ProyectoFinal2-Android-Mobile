package com.g18.ccp.data.remote.model.seller.order.product.order

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SellerOrderCreatedResponse(
    val code: Int,
    val data: SellerOrderCreatedData,
    val message: String,
    val status: String
) : Parcelable

@Parcelize
data class SellerOrderCreatedData(
    val date: String,
    val items: List<SellerOrderCreatedItem>,
    @SerializedName("order_id")
    val orderId: String,
    val status: String,
    val summary: String,
    val total: Double
) : Parcelable

@Parcelize
data class SellerOrderCreatedItem(
    val description: String,
    @SerializedName("image_url")
    val imageUrl: String,
    val price: Double,
    val quantity: Int,
    val title: String
) : Parcelable


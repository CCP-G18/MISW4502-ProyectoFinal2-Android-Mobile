package com.g18.ccp.data.remote.model.seller.order.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StockUpdate(
    @SerializedName("product_id")
    val productId: String,
    val name: String,
    @SerializedName("category")
    val categoryId: String,
) : Parcelable


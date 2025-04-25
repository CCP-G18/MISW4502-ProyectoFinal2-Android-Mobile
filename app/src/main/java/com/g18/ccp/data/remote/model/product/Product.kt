package com.g18.ccp.data.remote.model.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String,
    val name: String,
    @SerializedName("quantity")
    val leftAmount: Int,
    @SerializedName("unit_amount")
    val price: Float,
    @SerializedName("image_url")
    val imageUrl: String,
): Parcelable

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
    val imageUrl: String = "https://back.tiendainval.com/backend/admin/backend/web/archivosDelCliente/items/images/20210108100138no_image_product.png",
): Parcelable

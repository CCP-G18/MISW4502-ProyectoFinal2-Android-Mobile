package com.g18.ccp.data.remote.model.seller.order.product

import com.google.gson.annotations.SerializedName

data class SellerProductData(
    val id: String,
    @SerializedName("category_id")
    val categoryId: String,
    @SerializedName("created_at")
    val createdAt: String,
    val description: String,
    @SerializedName("image_url")
    val imageUrl: String? = "https://back.tiendainval.com/backend/admin/backend/web/archivosDelCliente/items/images/20210108100138no_image_product.png",
    val name: String,
    val quantity: Int,
    @SerializedName("unit_amount")
    val price: Float,
    @SerializedName("updated_at")
    val updatedAt: String,
)

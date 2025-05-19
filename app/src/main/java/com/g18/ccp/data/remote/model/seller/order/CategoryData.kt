package com.g18.ccp.data.remote.model.seller.order

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryData(
    @SerializedName("created_at")
    val createdAt: String,
    val id: String,
    val name: String,
    @SerializedName("updated_at")
    val updatedAt: String,
) : Parcelable

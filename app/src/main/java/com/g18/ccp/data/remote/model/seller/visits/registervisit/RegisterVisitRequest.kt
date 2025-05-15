package com.g18.ccp.data.remote.model.seller.visits.registervisit

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterVisitRequest(
    val observations: String,
    @SerializedName("customer_id")
    val customerId: String,
    @SerializedName("seller_id")
    val sellerId: String,
    @SerializedName("register_date")
    val date: String,
) : Parcelable


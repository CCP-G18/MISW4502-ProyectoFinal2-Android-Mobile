package com.g18.ccp.data.remote.model.seller.visits

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class VisitData(
    val id: String,
    val observations: String,
    @SerializedName("customer_id")
    val customerId: String,
    @SerializedName("seller_id")
    val sellerId: String,
    @SerializedName("register_date")
    val registerDate: Date,
) : Parcelable

package com.g18.ccp.data.remote.model.recommendation

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class RecommendationData(
    val id: String,
    @SerializedName("recommendation_date")
    val recommendationDate: Date,
    val recommendations: String,
    @SerializedName("seller_id")
    val sellerId: String,
    @SerializedName("customer_id")
    val customerId: String,
    @SerializedName("video_url")
    val videoUrl: String
): Parcelable

package com.g18.ccp.data.remote.model.recommendation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecommendationListResponse(
    val code: Int,
    val data: List<RecommendationData>,
    val message: String,
    val status: String
): Parcelable

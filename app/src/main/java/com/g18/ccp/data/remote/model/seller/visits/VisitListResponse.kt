package com.g18.ccp.data.remote.model.seller.visits

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VisitListResponse(
    val code: Int,
    val data: List<VisitData>,
    val message: String,
    val status: String
) : Parcelable

package com.g18.ccp.data.remote.model.seller.visits.registervisit

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterVisitResponse(
    val code: Int,
    val message: String,
    val status: String
) : Parcelable

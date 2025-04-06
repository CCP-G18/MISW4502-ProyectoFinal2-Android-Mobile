package com.g18.ccp.data.remote.model.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginErrorResponse(
    val code: Int,
    val error: String,
    val status: String
) : Parcelable

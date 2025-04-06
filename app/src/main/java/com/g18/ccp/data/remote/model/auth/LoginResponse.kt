package com.g18.ccp.data.remote.model.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
data class LoginResponse(
    val code: Int,
    val data: LoginData,
    val message: String,
    val status: String
) : Parcelable

@Parcelize
data class LoginData(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("user")
    val user: UserInfo
) : Parcelable

@Serializable
@Parcelize
data class UserInfo(
    val email: String,
    val id: String,
    val role: String,
    val username: String
): Parcelable


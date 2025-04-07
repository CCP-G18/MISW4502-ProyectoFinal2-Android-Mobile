package com.g18.ccp.data.remote.model.auth.register.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class RegisterClientResponse(
    val code: Int,
    val data: ClientData,
    val message: String,
    val status: String
) : Parcelable

@Parcelize
@Serializable
data class ClientData(
    val address: String,
    val city: String,
    val country: String,
    val identificationNumber: Long,
    val identificationType: String,
    val user: RegisteredUser
) : Parcelable

@Parcelize
@Serializable
data class RegisteredUser(
    val email: String,
    val lastname: String,
    val name: String,
    val role: String
) : Parcelable

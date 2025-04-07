package com.g18.ccp.data.remote.model.auth.register.client

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class RegisterClientRequest(
    val identificationType: String,
    val identificationNumber: Long,
    val country: String,
    val city: String,
    val address: String,
    val user: RegisterUser
) : Parcelable

@Serializable
@Parcelize
data class RegisterUser(
    val name: String,
    val lastname: String,
    val email: String,
    val password: String
) : Parcelable

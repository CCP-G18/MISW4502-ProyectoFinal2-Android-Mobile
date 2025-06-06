package com.g18.ccp.data.remote.model.seller

import android.os.Parcelable
import com.g18.ccp.core.constants.enums.IdentificationType
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomerData(
    val address: String,
    val city: String,
    val country: String,
    val email: String,
    val id: String,
    @SerializedName("identification_number")
    val identificationNumber: String,
    val identificationType: IdentificationType,
    val name: String,
) : Parcelable

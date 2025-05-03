package com.g18.ccp.data.local.model.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.g18.ccp.core.constants.enums.IdentificationType // Importa tu Enum

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey
    val id: String,

    val address: String?,
    val city: String?,
    val country: String?,
    val email: String?,
    val identificationNumber: String?,
    val identificationType: IdentificationType?,
    val name: String?
)

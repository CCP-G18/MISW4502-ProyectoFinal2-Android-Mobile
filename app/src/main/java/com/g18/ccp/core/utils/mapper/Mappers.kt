package com.g18.ccp.core.utils.mapper

import com.g18.ccp.core.constants.enums.IdentificationType
import com.g18.ccp.data.local.model.room.model.CustomerEntity
import com.g18.ccp.data.remote.model.seller.CustomerData


fun CustomerData.toEntity(): CustomerEntity {
    return CustomerEntity(
        id = this.id,
        address = this.address,
        city = this.city,
        country = this.country,
        email = this.email,
        identificationNumber = this.identificationNumber,
        identificationType = this.identificationType,
        name = this.name
    )
}

fun List<CustomerData>.toEntityList(): List<CustomerEntity> {
    return this.mapNotNull { it?.toEntity() }
}

fun CustomerEntity.toDomainModel(): CustomerData {
    return CustomerData(
        id = this.id,
        name = this.name ?: "N/A",
        address = this.address ?: "N/A",
        city = this.city ?: "N/A",
        country = this.country ?: "N/A",
        email = this.email ?: "N/A",
        identificationNumber = this.identificationNumber ?: "N/A",
        identificationType = this.identificationType ?: IdentificationType.CC
    )
}

fun List<CustomerEntity>.toDomainModelList(): List<CustomerData> {
    return this.map { it.toDomainModel() }
}

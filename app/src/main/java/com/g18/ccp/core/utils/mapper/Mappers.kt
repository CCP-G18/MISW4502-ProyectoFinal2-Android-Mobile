package com.g18.ccp.core.utils.mapper

import com.g18.ccp.core.constants.enums.IdentificationType
import com.g18.ccp.data.local.model.room.model.CategoryEntity
import com.g18.ccp.data.local.model.room.model.CustomerEntity
import com.g18.ccp.data.remote.model.seller.CustomerData
import com.g18.ccp.data.remote.model.seller.order.CategoryData


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

fun List<CustomerData>.toCustomerEntityList(): List<CustomerEntity> {
    return this.mapNotNull { it.toEntity() }
}

fun CustomerEntity.toDomainCustomerModel(): CustomerData {
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

fun List<CustomerEntity>.toDomainCustomerModelList(): List<CustomerData> {
    return this.map { it.toDomainCustomerModel() }
}

fun CategoryData.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        name = this.name,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}

fun List<CategoryData>.toCategoryEntityList(): List<CategoryEntity> {
    return this.map { it.toEntity() }
}

fun CategoryEntity.toDomainCategoryModel(): CategoryData {
    return CategoryData(
        id = this.id,
        name = this.name,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}

fun List<CategoryEntity>.toDomainCategoryModelList(): List<CategoryData> {
    return this.map { it.toDomainCategoryModel() }
}

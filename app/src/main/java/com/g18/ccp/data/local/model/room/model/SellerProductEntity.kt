package com.g18.ccp.data.local.model.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.g18.ccp.data.remote.model.seller.order.product.SellerProductData

@Entity(tableName = "seller_products")
data class SellerProductEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val createdAt: String,
    val description: String,
    val imageUrl: String,
    val name: String,
    var quantity: Int,
    val price: Float,
    val updatedAt: String
)

fun SellerProductEntity.toProductData(): SellerProductData {
    return SellerProductData(
        id = this.id,
        categoryId = this.categoryId,
        createdAt = this.createdAt,
        description = this.description,
        imageUrl = this.imageUrl,
        name = this.name,
        quantity = this.quantity,
        price = this.price,
        updatedAt = this.updatedAt
    )
}

fun List<SellerProductEntity>.toProductDataList(): List<SellerProductData> {
    return this.map { it.toProductData() }
}

fun SellerProductData.toSellerProductEntity(): SellerProductEntity {
    return SellerProductEntity(
        id = this.id,
        categoryId = this.categoryId,
        createdAt = this.createdAt,
        description = this.description,
        imageUrl = this.imageUrl
            ?: "https://back.tiendainval.com/backend/admin/backend/web/archivosDelCliente/items/images/20210108100138no_image_product.png",
        name = this.name,
        quantity = this.quantity, // Stock
        price = this.price,
        updatedAt = this.updatedAt
    )
}

fun List<SellerProductData>.toSellerProductEntityList(): List<SellerProductEntity> {
    return this.map { it.toSellerProductEntity() }
}

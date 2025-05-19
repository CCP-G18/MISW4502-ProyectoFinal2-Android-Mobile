package com.g18.ccp.data.local.model.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.g18.ccp.data.local.model.room.model.SellerProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SellerProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(products: List<SellerProductEntity>)

    // Obtiene productos por categoría, ordenados por precio (más caro primero)
    @Query("SELECT * FROM seller_products WHERE categoryId = :categoryId ORDER BY price DESC")
    fun getProductsByCategory(categoryId: String): Flow<List<SellerProductEntity>>

    @Query("SELECT * FROM seller_products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: String): SellerProductEntity?

    // Para actualizar el stock de un producto (ej. por WebSocket)
    @Update
    suspend fun updateProduct(product: SellerProductEntity)

    @Query("DELETE FROM seller_products WHERE categoryId = :categoryId")
    suspend fun clearProductsByCategory(categoryId: String)

    @Query("DELETE FROM seller_products")
    suspend fun clearAllProducts()
}

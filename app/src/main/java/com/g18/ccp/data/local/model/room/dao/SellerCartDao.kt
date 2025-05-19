package com.g18.ccp.data.local.model.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.g18.ccp.data.local.model.cart.seller.SellerCartItemWithProduct
import com.g18.ccp.data.local.model.room.model.SellerCartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SellerCartDao {

    @Transaction
    @Query(
        """
        SELECT
            sci.productId, sci.quantityInCart,
            sp.name, sp.description, sp.price, sp.imageUrl, sp.quantity AS stock
        FROM seller_cart_items_v2 sci
        INNER JOIN seller_products sp ON sci.productId = sp.id
    """
    )
    fun getAllCartItemsWithDetails(): Flow<List<SellerCartItemWithProduct>>

    @Query("SELECT * FROM seller_cart_items_v2 WHERE productId = :productId LIMIT 1")
    suspend fun getCartItemByProductId(productId: String): SellerCartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateItem(item: SellerCartItemEntity)

    @Query("DELETE FROM seller_cart_items_v2 WHERE productId = :productId")
    suspend fun deleteItem(productId: String)

    @Query("DELETE FROM seller_cart_items_v2")
    suspend fun clearCart()
}

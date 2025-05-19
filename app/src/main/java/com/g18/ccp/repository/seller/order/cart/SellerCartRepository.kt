package com.g18.ccp.repository.seller.order.cart

import com.g18.ccp.data.local.model.cart.seller.SellerCartItem
import com.g18.ccp.data.remote.model.seller.order.product.SellerProductData
import kotlinx.coroutines.flow.Flow

interface SellerCartRepository {
    fun getCartItemsWithDetails(): Flow<List<SellerCartItem>>
    suspend fun addItem(product: SellerProductData, quantity: Int): Result<Unit>
    suspend fun updateItemQuantity(productId: String, newQuantity: Int): Result<Unit>
    suspend fun removeItem(productId: String): Result<Unit>
    suspend fun clearCart(): Result<Unit>
}

package com.g18.ccp.repository.seller.order.cart

import android.util.Log
import com.g18.ccp.data.local.model.cart.seller.SellerCartItem
import com.g18.ccp.data.local.model.room.dao.SellerCartDao
import com.g18.ccp.data.local.model.room.model.SellerCartItemEntity
import com.g18.ccp.data.remote.model.seller.order.product.SellerProductData
import com.g18.ccp.repository.seller.order.category.product.SellerProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


class SellerCartRepositoryImpl(
    private val cartDao: SellerCartDao,
    private val productRepository: SellerProductRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SellerCartRepository {

    override fun getCartItemsWithDetails(): Flow<List<SellerCartItem>> {
        return cartDao.getAllCartItemsWithDetails().map { listWithProductDetails ->
            listWithProductDetails.map { item ->
                SellerCartItem(
                    product = SellerProductData(
                        id = item.productId,
                        name = item.name,
                        description = item.description,
                        price = item.price,
                        imageUrl = item.imageUrl
                            ?: "https://back.tiendainval.com/backend/admin/backend/web/archivosDelCliente/items/images/20210108100138no_image_product.png",
                        quantity = item.stock,
                        categoryId = "",
                        createdAt = "",
                        updatedAt = ""
                    ),
                    quantity = item.quantityInCart
                )
            }
        }
    }

    override suspend fun addItem(product: SellerProductData, quantity: Int): Result<Unit> =
        withContext(ioDispatcher) {
            if (quantity <= 0) return@withContext Result.failure(IllegalArgumentException("Cantidad debe ser positiva"))
            if (quantity > product.quantity) return@withContext Result.failure(
                IllegalArgumentException("Cantidad excede stock")
            )

            try {
                val existingItem = cartDao.getCartItemByProductId(product.id)
                val finalQuantity =
                    if (existingItem != null) existingItem.quantityInCart + quantity else quantity

                if (finalQuantity > product.quantity) {
                    return@withContext Result.failure(IllegalArgumentException("Cantidad total en carrito excede stock."))
                }
                cartDao.insertOrUpdateItem(
                    SellerCartItemEntity(
                        productId = product.id,
                        quantityInCart = finalQuantity
                    )
                )
                Log.d("CartRepo", "Item ${product.name} added/updated to $finalQuantity")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("CartRepo", "Error adding item", e)
                Result.failure(e)
            }
        }

    override suspend fun updateItemQuantity(productId: String, newQuantity: Int): Result<Unit> =
        withContext(ioDispatcher) {
            if (newQuantity < 0) return@withContext Result.failure(IllegalArgumentException("Cantidad negativa"))
            try {
                val itemInCart = cartDao.getCartItemByProductId(productId)
                if (itemInCart == null) return@withContext Result.failure(NoSuchElementException("Producto no en carrito"))

                // Necesitamos el stock original del producto para validar
                val productDetails = productRepository.getProductById(productId)
                if (productDetails == null) return@withContext Result.failure(
                    NoSuchElementException(
                        "Detalles del producto no encontrados"
                    )
                )

                if (newQuantity > productDetails.quantity) { // productDetails.quantity es el stock
                    return@withContext Result.failure(IllegalArgumentException("Cantidad excede stock"))
                }

                if (newQuantity == 0) {
                    cartDao.deleteItem(productId)
                } else {
                    cartDao.insertOrUpdateItem(itemInCart.copy(quantityInCart = newQuantity))
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("CartRepo", "Error updating quantity", e)
                Result.failure(e)
            }
        }

    override suspend fun removeItem(productId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            cartDao.deleteItem(productId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearCart(): Result<Unit> = withContext(ioDispatcher) {
        try {
            cartDao.clearCart()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

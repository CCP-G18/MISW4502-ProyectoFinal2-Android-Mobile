package com.g18.ccp.repository.seller.order.category.product

import com.g18.ccp.data.remote.model.seller.order.product.SellerProductData
import kotlinx.coroutines.flow.Flow

interface SellerProductRepository {
    fun getProductsForCategoryFromDB(categoryId: String): Flow<List<SellerProductData>>
    suspend fun refreshProductsForCategory(categoryId: String, customerId: String): Result<Unit>
    suspend fun getProductById(productId: String): SellerProductData?
}

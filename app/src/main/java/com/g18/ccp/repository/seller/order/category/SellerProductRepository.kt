package com.g18.ccp.repository.seller.order.category

import com.g18.ccp.data.remote.model.seller.order.CategoryData
import kotlinx.coroutines.flow.Flow

interface SellerProductRepository {
    suspend fun getCategories(): Flow<List<CategoryData>>

    fun getCategoryByCategoryId(categoryId: String): Flow<CategoryData?>

    suspend fun refreshCategories(): Result<Unit>
}

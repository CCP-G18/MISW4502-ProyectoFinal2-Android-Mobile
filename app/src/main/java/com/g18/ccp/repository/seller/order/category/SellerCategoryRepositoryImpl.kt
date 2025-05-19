package com.g18.ccp.repository.seller.order.category

import android.util.Log
import com.g18.ccp.core.utils.mapper.toCategoryEntityList
import com.g18.ccp.core.utils.mapper.toDomainCategoryModel
import com.g18.ccp.core.utils.mapper.toDomainCategoryModelList
import com.g18.ccp.data.local.model.room.dao.CategoryDao
import com.g18.ccp.data.remote.model.seller.order.CategoryData
import com.g18.ccp.data.remote.service.seller.order.category.CategoryService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SellerCategoryRepositoryImpl(
    private val categoryService: CategoryService,
    private val categoryDao: CategoryDao
) : SellerCategoryRepository {
    override suspend fun getCategories(): Flow<List<CategoryData>> {
        Log.d("SellerProductRepositoryImpl", "getCategories called - Reading from Room Flow")
        return categoryDao.getAllCategories()
            .map { entities ->
                Log.d(
                    "SellerProductRepositoryImpl",
                    "Room emitted ${entities.size} categories. Mapping to Domain."
                )
                entities.toDomainCategoryModelList()
            }
    }

    override fun getCategoryByCategoryId(categoryId: String): Flow<CategoryData?> {
        Log.d(
            "SellerProductRepositoryImpl",
            "getCategoryById called for ID: $categoryId - Reading from Room Flow"
        )
        return categoryDao.getCategoryById(categoryId).map { entity ->
            Log.d(
                "SellerProductRepositoryImpl",
                "Room emitted category for ID $categoryId. Exists: ${entity != null}. Mapping to Domain."
            )
            entity?.toDomainCategoryModel()
        }
    }

    override suspend fun refreshCategories(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.i("SellerProductRepositoryImpl", "Attempting network refresh...")
            val response = categoryService.getCategories()
            val entitiesToSave = response.data.toCategoryEntityList()
            Log.d(
                "SellerProductRepositoryImpl",
                "Network success. Mapping ${entitiesToSave.size} entities for Room."
            )

            categoryDao.insertAll(entitiesToSave)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SellerProductRepositoryImpl", "Network/Refresh Error", e)
            Result.failure(e)
        }
    }
}

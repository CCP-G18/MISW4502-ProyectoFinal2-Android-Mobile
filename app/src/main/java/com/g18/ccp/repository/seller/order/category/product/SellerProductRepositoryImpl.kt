package com.g18.ccp.repository.seller.order.category.product

import android.util.Log
import com.g18.ccp.data.local.model.room.dao.SellerProductDao
import com.g18.ccp.data.local.model.room.model.toProductData
import com.g18.ccp.data.local.model.room.model.toProductDataList
import com.g18.ccp.data.local.model.room.model.toSellerProductEntityList
import com.g18.ccp.data.remote.model.seller.order.product.SellerProductData
import com.g18.ccp.data.remote.service.seller.order.product.SellerProductService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SellerProductRepositoryImpl(
    private val productApiService: SellerProductService,
    private val productDao: SellerProductDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SellerProductRepository {

    override fun getProductsForCategoryFromDB(categoryId: String): Flow<List<SellerProductData>> {
        return productDao.getProductsByCategory(categoryId).map { entities ->
            entities.toProductDataList()
        }
    }

    override suspend fun getProductById(productId: String): SellerProductData? =
        withContext(ioDispatcher) {
            productDao.getProductById(productId)?.toProductData()
        }

    override suspend fun refreshProductsForCategory(
        categoryId: String,
        customerId: String
    ): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                Log.d("ProductRepo", "Refreshing products for category: $categoryId")
                val response = productApiService.getProducts(
                    categoryId
                )
                if (response.isSuccessful && response.body() != null) {
                    val networkProducts = response.body()!!.data
                    productDao.insertAllProducts(networkProducts.toSellerProductEntityList())
                    Log.i(
                        "ProductRepo",
                        "Successfully refreshed ${networkProducts.size} products for category $categoryId."
                    )
                    Result.success(Unit)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error"
                    Log.e(
                        "ProductRepo",
                        "API Error fetching products for category $categoryId: ${response.code()} - $errorBody"
                    )
                    Result.failure(Exception("API Error ${response.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                Log.e("ProductRepo", "Exception refreshing products for category $categoryId", e)
                Result.failure(e)
            }
        }
    }
}

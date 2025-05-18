package com.g18.ccp.data.remote.service.seller.order.category

import com.g18.ccp.data.remote.model.seller.order.CategoryListResponse
import retrofit2.http.GET

interface CategoryService {
    @GET("products/categories")
    suspend fun getCategories(): CategoryListResponse
}

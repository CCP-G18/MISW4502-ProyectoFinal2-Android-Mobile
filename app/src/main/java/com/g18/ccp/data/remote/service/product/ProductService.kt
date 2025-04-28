package com.g18.ccp.data.remote.service.product

import com.g18.ccp.data.remote.model.product.ProductResponse
import retrofit2.http.GET

interface ProductService {
    @GET("products")
    suspend fun getProducts(): ProductResponse
}

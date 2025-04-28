package com.g18.ccp.repository.product

import com.g18.ccp.data.remote.model.product.ProductResponse

interface ProductRepository {
    suspend fun getProducts(): ProductResponse
}

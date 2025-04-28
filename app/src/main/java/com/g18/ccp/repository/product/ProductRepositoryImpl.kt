package com.g18.ccp.repository.product

import com.g18.ccp.data.remote.model.product.ProductResponse
import com.g18.ccp.data.remote.service.product.ProductService

class ProductRepositoryImpl(private val productService: ProductService) : ProductRepository {
    override suspend fun getProducts(): ProductResponse = productService.getProducts()
}

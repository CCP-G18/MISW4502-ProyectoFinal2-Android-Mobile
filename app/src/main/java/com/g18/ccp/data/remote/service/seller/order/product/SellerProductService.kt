package com.g18.ccp.data.remote.service.seller.order.product

import com.g18.ccp.data.remote.model.seller.order.product.SellerProductListResponse
import com.g18.ccp.data.remote.model.seller.order.product.order.SellerOrderCreatedResponse
import com.g18.ccp.data.remote.model.seller.order.product.order.SellerOrderRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SellerProductService {
    @GET("products/category/{categoryId} ")
    suspend fun getProducts(
        @Path("categoryId") categoryId: String
    ): Response<SellerProductListResponse>

    @POST("orders/seller")
    suspend fun placeOrder(@Body order: SellerOrderRequest): Result<SellerOrderCreatedResponse>
}

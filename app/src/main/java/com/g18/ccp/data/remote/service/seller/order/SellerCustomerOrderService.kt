package com.g18.ccp.data.remote.service.seller.order

import com.g18.ccp.data.remote.model.seller.order.product.order.SellerCustomerOrdersResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface SellerCustomerOrderService {
    @GET("orders/customer/{customerId}")
    suspend fun getCustomerOrders(
        @Path("customerId") customerId: String
    ): SellerCustomerOrdersResponse
}

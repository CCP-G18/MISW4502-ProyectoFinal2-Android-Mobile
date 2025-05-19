package com.g18.ccp.repository.seller.order.createorder

import com.g18.ccp.data.remote.model.seller.order.product.order.SellerCustomerOrderData

interface SellerCustomerOrdersRepository {
    suspend fun fetchOrdersForCustomer(customerId: String): Result<List<SellerCustomerOrderData>>
}

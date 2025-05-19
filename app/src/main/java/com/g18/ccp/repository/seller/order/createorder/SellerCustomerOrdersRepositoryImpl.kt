package com.g18.ccp.repository.seller.order.createorder

import com.g18.ccp.data.remote.model.seller.order.product.order.SellerCustomerOrderData
import com.g18.ccp.data.remote.service.seller.order.SellerCustomerOrderService

class SellerCustomerOrdersRepositoryImpl(
    private val service: SellerCustomerOrderService
) : SellerCustomerOrdersRepository {
    override suspend fun fetchOrdersForCustomer(customerId: String): Result<List<SellerCustomerOrderData>> {
        return try {
            val resp = service.getCustomerOrders(customerId)
            if (resp.code == 200) Result.success(resp.data)
            else Result.failure(RuntimeException(resp.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

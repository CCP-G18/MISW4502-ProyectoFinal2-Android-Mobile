package com.g18.ccp.repository.seller.order.cart

import com.g18.ccp.data.local.model.cart.seller.SellerCartItem

interface SellerOrderRepository {
    suspend fun placeOrder(customerId: String, items: List<SellerCartItem>): Result<Unit>
}

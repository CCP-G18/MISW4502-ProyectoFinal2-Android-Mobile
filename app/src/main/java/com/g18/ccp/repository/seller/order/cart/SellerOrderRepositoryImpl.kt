package com.g18.ccp.repository.seller.order.cart

import com.g18.ccp.data.local.model.cart.seller.SellerCartItem
import com.g18.ccp.data.remote.model.seller.order.product.order.SellerOrderItem
import com.g18.ccp.data.remote.model.seller.order.product.order.SellerOrderRequest
import com.g18.ccp.data.remote.service.seller.order.product.SellerProductService
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SellerOrderRepositoryImpl(
    private val productApiService: SellerProductService,
) : SellerOrderRepository {
    override suspend fun placeOrder(customerId: String, items: List<SellerCartItem>): Result<Unit> {
        return try {
            // Build ISO date string, e.g. "2025-05-02"
            val date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            // Sum up line totals
            val total = items.sumOf { it.quantity * it.product.price.toDouble() }
            // Map your cart items to the payload format
            val items = items.map { cart ->
                SellerOrderItem(
                    id = cart.product.id,
                    quantity = cart.quantity,
                    price = cart.product.price.toDouble()
                )
            }
            val request = SellerOrderRequest(
                date = date,
                total = total,
                customerId = customerId,
                items = items
            )
            val response = productApiService.placeOrder(request)
            if (response.isSuccess) Result.success(Unit)
            else Result.failure(RuntimeException("Error placing order: ${response}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

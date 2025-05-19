package com.g18.ccp.presentation.seller.order.createorder

import com.g18.ccp.data.remote.model.seller.order.product.order.SellerCustomerOrderData

sealed class SellerCustomerOrdersUiState {
    object Loading : SellerCustomerOrdersUiState()
    data class Success(val orders: List<SellerCustomerOrderData>) : SellerCustomerOrdersUiState()
    data class Error(val message: String) : SellerCustomerOrdersUiState()
}

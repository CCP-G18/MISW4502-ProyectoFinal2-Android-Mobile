package com.g18.ccp.presentation.seller.order.category.products

import com.g18.ccp.data.remote.model.seller.order.product.SellerProductData

sealed interface SellerCategoryProductsUiState {
    data object Loading : SellerCategoryProductsUiState
    data class Success(
        val categoryName: String,
        val products: List<SellerProductData>,
        val searchQuery: String = ""
    ) : SellerCategoryProductsUiState

    data class Error(val message: String) : SellerCategoryProductsUiState
}

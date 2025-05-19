package com.g18.ccp.presentation.seller.order.category

import com.g18.ccp.data.remote.model.seller.order.CategoryData

sealed interface CategoryUiState {
    data object Loading : CategoryUiState

    data class Success(
        val categories: List<CategoryData>,
        val searchQuery: String
    ) : CategoryUiState

    data class Error(
        val message: String,
        val searchQuery: String
    ) : CategoryUiState
}

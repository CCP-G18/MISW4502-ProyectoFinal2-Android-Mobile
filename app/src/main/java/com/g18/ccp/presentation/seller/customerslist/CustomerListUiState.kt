package com.g18.ccp.presentation.seller.customerslist

import com.g18.ccp.data.remote.model.seller.CustomerData

sealed interface CustomerListUiState {
    data object Loading : CustomerListUiState

    data class Success(
        val customers: List<CustomerData>,
        val searchQuery: String
    ) : CustomerListUiState

    data class Error(
        val message: String,
        val searchQuery: String
    ) : CustomerListUiState
}

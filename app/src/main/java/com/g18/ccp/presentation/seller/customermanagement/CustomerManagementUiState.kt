package com.g18.ccp.presentation.seller.customermanagement

import com.g18.ccp.data.remote.model.seller.CustomerData

sealed interface CustomerManagementUiState {
    data object Loading : CustomerManagementUiState
    data class Success(val customer: CustomerData) : CustomerManagementUiState
    data class Error(val message: String) : CustomerManagementUiState
    data object NotFound : CustomerManagementUiState
}

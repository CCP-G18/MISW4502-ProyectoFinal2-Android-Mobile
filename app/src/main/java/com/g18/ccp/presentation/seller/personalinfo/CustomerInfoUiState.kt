package com.g18.ccp.presentation.seller.personalinfo

import com.g18.ccp.data.remote.model.seller.CustomerData

sealed interface CustomerInfoUiState {
    data object Loading : CustomerInfoUiState
    data class Success(val customer: CustomerData) : CustomerInfoUiState
    data class Error(val message: String) : CustomerInfoUiState
    data object NotFound : CustomerInfoUiState
}

package com.g18.ccp.core.utils.auth

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    object Success : UiState()
    data class Error(val exception: Exception?) : UiState()
}

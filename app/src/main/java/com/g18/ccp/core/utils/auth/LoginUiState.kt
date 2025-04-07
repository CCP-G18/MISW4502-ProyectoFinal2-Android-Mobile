package com.g18.ccp.core.utils.auth

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val exception: Exception?) : LoginUiState()
}

package com.g18.ccp.presentation.seller.customervisit.register

data class RegisterVisitScreenUiState(
    val customerName: String = "",
    val selectedDate: String = "",
    val observations: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showDatePicker: Boolean = false
)


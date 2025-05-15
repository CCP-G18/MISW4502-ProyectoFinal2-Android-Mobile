package com.g18.ccp.presentation.seller.customervisit.list

sealed interface VisitsScreenUiState {
    data object Loading : VisitsScreenUiState
    data class Success(
        val customerName: String,
        val visits: List<VisitDisplayItem>
    ) : VisitsScreenUiState

    data class Error(val message: String) : VisitsScreenUiState
}

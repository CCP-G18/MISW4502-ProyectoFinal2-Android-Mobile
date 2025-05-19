package com.g18.ccp.presentation.seller.order.createorder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.core.constants.CUSTOMER_ID_ARG
import com.g18.ccp.repository.seller.order.createorder.SellerCustomerOrdersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SellerCustomerOrdersViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: SellerCustomerOrdersRepository,
) : ViewModel() {

    private val customerId: String = checkNotNull(savedStateHandle[CUSTOMER_ID_ARG])

    private val _uiState =
        MutableStateFlow<SellerCustomerOrdersUiState>(SellerCustomerOrdersUiState.Loading)
    val uiState: StateFlow<SellerCustomerOrdersUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _uiState.value = SellerCustomerOrdersUiState.Loading
            val result = repo.fetchOrdersForCustomer(customerId)
            _uiState.value = result
                .fold(
                    onSuccess = { SellerCustomerOrdersUiState.Success(it) },
                    onFailure = { SellerCustomerOrdersUiState.Error(it.message ?: "Unknown error") }
                )
        }
    }
}

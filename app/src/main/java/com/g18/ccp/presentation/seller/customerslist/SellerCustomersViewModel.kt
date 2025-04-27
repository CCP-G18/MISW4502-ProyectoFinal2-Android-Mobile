package com.g18.ccp.presentation.seller.customerslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.data.remote.model.seller.CustomerData
import com.g18.ccp.repository.seller.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SellerCustomersViewModel(
    private val customerRepository: CustomerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<CustomerListUiState>(CustomerListUiState.Loading)
    val uiState: StateFlow<CustomerListUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredCustomers: StateFlow<List<CustomerData>> = uiState
        .combine(searchQuery) { state, query ->
            when (state) {
                is CustomerListUiState.Success -> {
                    if (query.isBlank()) {
                        state.customers
                    } else {
                        state.customers.filter { customer ->
                            customer.name.contains(query, ignoreCase = true) ||
                                    customer.identificationNumber.contains(query, ignoreCase = true)
                        }
                    }
                }

                else -> emptyList()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    init {
        fetchCustomers()
    }

    fun fetchCustomers() {
        viewModelScope.launch {
            _uiState.value = CustomerListUiState.Loading
            try {
                val response = customerRepository.getCustomers()
                if (response.status.equals("success", ignoreCase = true)) {
                    _uiState.value = CustomerListUiState.Success(
                        customers = response.data,
                        searchQuery = _searchQuery.value
                    )
                } else {
                    val errorMsg = "Error ${response.code}: ${response.message}"
                    _uiState.value = CustomerListUiState.Error(
                        message = errorMsg,
                        searchQuery = _searchQuery.value
                    )
                }
            } catch (e: Exception) {
                _uiState.value = CustomerListUiState.Error(
                    message = e.message ?: "Error desconocido",
                    searchQuery = _searchQuery.value
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}

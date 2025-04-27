package com.g18.ccp.presentation.seller.customerslist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.data.remote.model.seller.CustomerData
import com.g18.ccp.repository.seller.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
                    val customersData = state.customers
                    if (query.isBlank()) {
                        customersData
                    } else {
                        customersData.filter { customerData ->
                            customerData.name.contains(
                                query,
                                ignoreCase = true
                            ) || customerData.identificationNumber.contains(
                                query,
                                ignoreCase = true
                            ) || customerData.address.contains(
                                query,
                                ignoreCase = true
                            )
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
        observeCustomersFromRoom()
        triggerRefresh(isInitialLoad = true)
    }

    private fun observeCustomersFromRoom() {
        viewModelScope.launch {
            Log.d(
                "SellerCustomersVM",
                "Starting to observe customers from Room (Flow<CustomerData>)..."
            )
            customerRepository.getCustomers()
                .catch { exception ->
                    Log.e("SellerCustomersVM", "Error collecting from Room", exception)
                    _uiState.value = CustomerListUiState.Error(
                        message = "Error DB: ${exception.message}",
                        searchQuery = _searchQuery.value
                    )
                }
                .collect { customerDataList ->
                    Log.d(
                        "SellerCustomersVM",
                        "Received ${customerDataList.size} CustomerData from Room Flow."
                    )

                    _uiState.value = CustomerListUiState.Success(
                        customers = customerDataList,
                        searchQuery = _searchQuery.value
                    )
                }
        }
    }

    fun triggerRefresh(isInitialLoad: Boolean = false) {
        viewModelScope.launch {
            if (!isInitialLoad && _uiState.value is CustomerListUiState.Success) {
                Log.d("SellerCustomersVM", "Triggering manual refresh...")
            } else {
                _uiState.value = CustomerListUiState.Loading
                Log.d("SellerCustomersVM", "Triggering initial refresh...")
            }

            val refreshResult = customerRepository.refreshCustomers()

            if (refreshResult.isFailure) {
                Log.e(
                    "SellerCustomersVM",
                    "Refresh failed: ${refreshResult.exceptionOrNull()?.message}"
                )
                _uiState.value = CustomerListUiState.Error(
                    message = "Fallo al refrescar: ${refreshResult.exceptionOrNull()?.message}",
                    searchQuery = _searchQuery.value
                )
            } else {
                Log.d("SellerCustomersVM", "Refresh successful. Room Flow will update the list.")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}

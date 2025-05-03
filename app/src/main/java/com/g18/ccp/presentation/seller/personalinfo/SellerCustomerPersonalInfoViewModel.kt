package com.g18.ccp.presentation.seller.personalinfo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.core.constants.CUSTOMER_ID_ARG
import com.g18.ccp.repository.seller.CustomerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SellerCustomerPersonalInfoViewModel(
    savedStateHandle: SavedStateHandle,
    customerRepository: CustomerRepository
) : ViewModel() {

    private val customerId: String = checkNotNull(savedStateHandle[CUSTOMER_ID_ARG]) {
        "Customer ID no fue pasado a SellerCustomerPersonalInfoViewModel"
    }

    val uiState: StateFlow<CustomerInfoUiState> = customerRepository.getCustomerById(customerId)
        .map { customerDataFromRoom ->
            if (customerDataFromRoom != null) {
                CustomerInfoUiState.Success(customerDataFromRoom)
            } else {
                CustomerInfoUiState.NotFound
            }
        }
        .catch { e ->
            emit(CustomerInfoUiState.Error("Error al cargar información: ${e.message}"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CustomerInfoUiState.Loading
        )
}

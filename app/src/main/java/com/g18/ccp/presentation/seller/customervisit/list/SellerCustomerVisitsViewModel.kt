package com.g18.ccp.presentation.seller.customervisit.list

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.data.remote.model.auth.UserInfo
import com.g18.ccp.repository.seller.CustomerRepository
import com.g18.ccp.repository.seller.customervisit.VisitRepository
import com.g18.ccp.repository.user.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class SellerCustomerVisitsViewModel(
    savedStateHandle: SavedStateHandle,
    private val visitRepository: VisitRepository,
    private val customerRepository: CustomerRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val customerId: String = checkNotNull(savedStateHandle["customerId"])
    private val _uiState = MutableStateFlow<VisitsScreenUiState>(VisitsScreenUiState.Loading)
    val uiState: StateFlow<VisitsScreenUiState> = _uiState

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = VisitsScreenUiState.Loading
            Log.d("VisitsVM", "Loading initial data for customer: $customerId")

            var customerName = "Cliente"
            try {
                val customerData = customerRepository.getCustomerById(customerId)
                    .firstOrNull()
                customerData?.name?.let { name ->
                    customerName = name
                }
                Log.d("VisitsVM", "Customer name loaded: $customerName")
            } catch (e: Exception) {
                Log.e("VisitsVM", "Error fetching customer name", e)
            }

            val visitsResult = visitRepository.getVisitsForCustomer(customerId)

            visitsResult.onSuccess { visitDataList ->
                Log.d("VisitsVM", "Successfully fetched ${visitDataList.size} visits.")
                val displayVisitsDeferred = visitDataList.map { visitData ->
                    async {
                        val sellerInfo: UserInfo? =
                            userRepository.getUserInfoById(visitData.sellerId)
                        val displayDate = formatDate(visitData.registerDate)
                        VisitDisplayItem(
                            id = visitData.id,
                            registerDate = visitData.registerDate,
                            displayDate = displayDate,
                            observations = visitData.observations,
                            customerId = visitData.customerId,
                            sellerId = visitData.sellerId,
                            sellerName = sellerInfo?.username ?: "",
                            sellerEmail = sellerInfo?.email ?: ""
                        )
                    }
                }
                val displayVisits =
                    displayVisitsDeferred.awaitAll().sortedByDescending { it.registerDate }

                _uiState.value = VisitsScreenUiState.Success(
                    customerName = customerName,
                    visits = displayVisits
                )
            }
            visitsResult.onFailure { exception ->
                Log.e("VisitsVM", "Failed to fetch visits", exception)

                _uiState.value = VisitsScreenUiState.Success(
                    customerName = customerName,
                    visits = listOf()
                )
            }
        }
    }

    private fun formatDate(date: java.util.Date): String {
        return try {
            val outputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            Log.e("VisitDataMapping", "Error formatting date object: $date", e)
            date.toString()
        }
    }

    fun deleteVisit(visitId: String) { /* TODO */
    }
}

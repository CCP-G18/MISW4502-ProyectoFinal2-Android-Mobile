package com.g18.ccp.presentation.seller.customervisit.register

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.core.constants.CUSTOMER_ID_ARG
import com.g18.ccp.repository.seller.CustomerRepository
import com.g18.ccp.repository.seller.customervisit.VisitRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class SellerRegisterVisitViewModel(
    private val savedStateHandle: SavedStateHandle, // private val para usarlo en loadInitialData
    private val visitRepository: VisitRepository,
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val customerId: String = checkNotNull(savedStateHandle[CUSTOMER_ID_ARG])

    private val _uiState = MutableStateFlow(RegisterVisitScreenUiState())
    val uiState: StateFlow<RegisterVisitScreenUiState> = _uiState.asStateFlow()


    fun loadInitialData() {
        viewModelScope.launch {
            val currentDateFormatted = SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            ).format(Calendar.getInstance().time)
            var fetchedCustomerName = "Cliente ID: $customerId" // Default

            try {
                val customer = customerRepository.getCustomerById(customerId).firstOrNull()
                customer?.name?.let { name ->
                    fetchedCustomerName = name
                }
            } catch (e: Exception) {
                Log.e("RegisterVisitVM", "Error fetching customer name for initial data", e)
            }

            _uiState.value = RegisterVisitScreenUiState(
                customerName = fetchedCustomerName,
                selectedDate = currentDateFormatted
            )
        }
    }

    private fun formatDateForBackend(dateString_ddMMyyyy: String): String? {
        return try {
            val parser = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = parser.parse(dateString_ddMMyyyy)
            date?.let { formatter.format(it) }
        } catch (e: ParseException) {
            Log.e("RegisterVisitVM", "Error parsing date for backend: $dateString_ddMMyyyy", e)
            null
        }
    }

    // En SellerRegisterVisitViewModel
    fun onDateSelected(selectedMillisUtc: Long?) {
        _uiState.update { it.copy(showDatePicker = false) }

        selectedMillisUtc?.let { millis ->
            val selectedDate =
                Instant.ofEpochMilli(millis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()

            val today = LocalDate.now(ZoneOffset.UTC)

            if (!selectedDate.isAfter(today)) {
                val displayFormatter =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
                _uiState.update {
                    it.copy(
                        selectedDate = selectedDate.format(displayFormatter),
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update { it.copy(errorMessage = "No se puede seleccionar una fecha futura.") }
            }
        }
    }

    fun onObservationsChanged(newObservations: String) {
        _uiState.update { it.copy(observations = newObservations) }
    }

    fun onShowDatePicker(show: Boolean) {
        _uiState.update { it.copy(showDatePicker = show) }
    }

    fun saveVisit(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onSuccessNavigation: () -> Unit
    ) {
        val currentState = _uiState.value
        val dateForBackend = formatDateForBackend(currentState.selectedDate)

        if (dateForBackend == null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Formato de fecha inválido."
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(dispatcher) {
            val result = visitRepository.registerVisit(
                customerId = customerId,
                date = dateForBackend,
                observations = currentState.observations.ifBlank { " " }
            )

            result.onSuccess { apiResponse ->
                Log.i("RegisterVisitVM", "Visit saved successfully: ${apiResponse.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                }
                onSuccessNavigation()
            }
            result.onFailure { exception ->
                Log.e("RegisterVisitVM", "Failed to save visit", exception)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al guardar visita: ${exception.message}"
                    )
                }
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

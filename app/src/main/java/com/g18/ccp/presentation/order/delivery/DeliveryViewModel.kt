package com.g18.ccp.presentation.order.delivery

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.core.constants.enums.OrderStatus
import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.data.remote.model.order.Order
import com.g18.ccp.repository.order.OrdersRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DeliveryViewModel(
    private val repository: OrdersRepository
) : ViewModel() {

    val uiState = mutableStateOf<Output<List<Order>>>(Output.Loading())

    fun loadOrders() {
        uiState.value = Output.Loading()
        viewModelScope.launch {
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val today = LocalDate.now()
                val result = repository.getOrders().filter {
                    val date = LocalDate.parse(it.date, formatter)
                    (it.status == OrderStatus.PREPARING ||
                            it.status == OrderStatus.ON_ROUTE) && !date.isBefore(today)
                }
                uiState.value = Output.Success(result)
            } catch (e: Exception) {
                uiState.value = Output.Failure(e)
            }
        }
    }
}

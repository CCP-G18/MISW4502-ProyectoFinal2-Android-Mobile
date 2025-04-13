package com.g18.ccp.presentation.order

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.data.remote.model.order.Order
import com.g18.ccp.repository.order.OrdersRepository
import kotlinx.coroutines.launch

class OrdersViewModel (
    private val repository: OrdersRepository
) : ViewModel() {

    val uiState = mutableStateOf<Output<List<Order>>>(Output.Loading())

    fun loadOrders() {
        uiState.value = Output.Loading()
        viewModelScope.launch {
            try {
                val result = repository.getOrders()
                uiState.value = Output.Success(result)
            } catch (e: Exception) {
                uiState.value = Output.Failure(e)
            }
        }
    }
}

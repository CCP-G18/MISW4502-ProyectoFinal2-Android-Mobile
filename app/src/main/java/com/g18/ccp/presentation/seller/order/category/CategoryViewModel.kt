package com.g18.ccp.presentation.seller.order.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.data.remote.model.seller.order.CategoryData
import com.g18.ccp.repository.seller.order.category.SellerProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryViewModel(
    private val categoryRepository: SellerProductRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredCategories: StateFlow<List<CategoryData>> = uiState
        .combine(searchQuery) { state, query ->
            when (state) {
                is CategoryUiState.Success -> {
                    val categoriesData = state.categories
                    if (query.isBlank()) {
                        categoriesData
                    } else {
                        categoriesData.filter { categoryData ->
                            categoryData.name.contains(
                                query,
                                ignoreCase = true
                            ) || categoryData.name.contains(
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
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )


    private suspend fun observeCategoriesFromRoom() {
        withContext(dispatcher) {
            Log.d(
                "SellerCustomersVM",
                "Starting to observe customers from Room (Flow<CustomerData>)..."
            )
            categoryRepository.getCategories()
                .catch { exception ->
                    Log.e("SellerCustomersVM", "Error collecting from Room", exception)
                    _uiState.value = CategoryUiState.Error(
                        message = "Error DB: ${exception.message}",
                        searchQuery = _searchQuery.value
                    )
                }
                .collect { categoryDataList ->
                    Log.d(
                        "SellerCustomersVM",
                        "Received ${categoryDataList.size} CustomerData from Room Flow."
                    )

                    _uiState.value = CategoryUiState.Success(
                        categories = categoryDataList.sortedBy { it.name.lowercase() },
                        searchQuery = _searchQuery.value
                    )
                }
        }
    }

    private suspend fun triggerRefresh(isInitialLoad: Boolean = false) {
        if (!isInitialLoad && _uiState.value is CategoryUiState.Success) {
            Log.d("SellerCustomersVM", "Triggering manual refresh...")
        } else {
            _uiState.value = CategoryUiState.Loading
            Log.d("SellerCustomersVM", "Triggering initial refresh...")
        }

        val refreshResult = categoryRepository.refreshCategories()

        if (refreshResult.isFailure) {
            Log.e(
                "SellerCustomersVM",
                "Refresh failed: ${refreshResult.exceptionOrNull()?.message}"
            )
            _uiState.value = CategoryUiState.Error(
                message = "Fallo al refrescar: ${refreshResult.exceptionOrNull()?.message}",
                searchQuery = _searchQuery.value
            )
        } else {
            Log.d("SellerCustomersVM", "Refresh successful. Room Flow will update the list.")
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun start(isInitialLoad: Boolean = true) {
        viewModelScope.launch(dispatcher) { observeCategoriesFromRoom() }
        viewModelScope.launch(dispatcher) { triggerRefresh(isInitialLoad) }
    }
}


package com.g18.ccp.presentation.seller.order.category.products

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.core.constants.CATEGORY_ID_ARG
import com.g18.ccp.core.constants.CUSTOMER_ID_ARG
import com.g18.ccp.data.local.model.cart.seller.SellerCartItem
import com.g18.ccp.data.remote.model.seller.order.product.SellerProductData
import com.g18.ccp.presentation.seller.order.category.products.SellerCategoryProductsUiState.Error
import com.g18.ccp.presentation.seller.order.category.products.SellerCategoryProductsUiState.Loading
import com.g18.ccp.presentation.seller.order.category.products.SellerCategoryProductsUiState.Success
import com.g18.ccp.repository.seller.order.cart.SellerCartRepository
import com.g18.ccp.repository.seller.order.category.product.SellerProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val POLLING_DELAY_TIME = 5000L

class SellerCategoryProductsViewModel(
    savedStateHandle: SavedStateHandle,
    private val productRepository: SellerProductRepository,
    private val cartRepository: SellerCartRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val categoryId: String = checkNotNull(savedStateHandle[CATEGORY_ID_ARG])
    private val customerId: String = checkNotNull(savedStateHandle[CUSTOMER_ID_ARG])

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<SellerCategoryProductsUiState>(Loading)
    val uiState: StateFlow<SellerCategoryProductsUiState> = _uiState.asStateFlow()

    private val _cartItems = MutableStateFlow<List<SellerCartItem>>(emptyList())
    val cartItems: StateFlow<List<SellerCartItem>> = _cartItems.asStateFlow()

    // Keep full list for filtering
    private var allProducts: List<SellerProductData> = emptyList()

    private var isPolling = true

    fun observeCartItems() {
        viewModelScope.launch(ioDispatcher) {
            cartRepository.getCartItemsWithDetails()
                .collectLatest { items ->
                    _cartItems.value = items
                }
        }
    }

    fun loadAndObserveProducts() {
        viewModelScope.launch(ioDispatcher) {
            productRepository.getProductsForCategoryFromDB(categoryId)
                .collectLatest { list ->
                    allProducts = list
                    val filtered = applySearch(allProducts, _searchQuery.value)
                    _uiState.value = Success(
                        categoryName = categoryId,
                        products = filtered,
                        searchQuery = _searchQuery.value
                    )
                }
        }
        viewModelScope.launch(ioDispatcher) {
            _uiState.value = Loading
            do {
                val result = productRepository.refreshProductsForCategory(categoryId, customerId)
                if (result.isFailure) {
                    _uiState.value = Error(
                        message = "Failed to refresh products: ${result.exceptionOrNull()?.message}"
                    )
                }
                delay(POLLING_DELAY_TIME)
            } while (isPolling)
        }
    }

    fun startPolling() {
        isPolling = true
    }

    fun stopPolling() {
        isPolling = false
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        val filtered = applySearch(allProducts, query)
        (_uiState.value as? Success)?.let { state ->
            _uiState.value = state.copy(
                products = filtered,
                searchQuery = query
            )
        }
    }

    private fun applySearch(
        list: List<SellerProductData>,
        query: String
    ): List<SellerProductData> = if (query.isBlank()) list
    else list.filter { it.name.contains(query, ignoreCase = true) }

    /**
     * Adjust the quantity of a cart item, adding or updating as needed.
     */
    fun updateCartItem(productId: String, newQuantity: Int) {
        viewModelScope.launch(ioDispatcher) {
            val product = allProducts.find { it.id == productId } ?: return@launch
            when {
                newQuantity <= 0 -> cartRepository.removeItem(productId)
                newQuantity > product.quantity -> Unit
                else -> {
                    val existingQty = _cartItems.value.find { it.product.id == productId }?.quantity
                    if (existingQty != null) {
                        cartRepository.updateItemQuantity(productId, newQuantity)
                    } else {
                        cartRepository.addItem(product, newQuantity)
                    }
                }
            }
        }
    }
}

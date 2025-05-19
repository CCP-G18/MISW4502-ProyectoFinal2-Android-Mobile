package com.g18.ccp.presentation.seller.order.category.products.cart

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.core.constants.CUSTOMER_ID_ARG
import com.g18.ccp.data.local.model.cart.seller.SellerCartItem
import com.g18.ccp.repository.seller.order.cart.SellerCartRepository
import com.g18.ccp.repository.seller.order.cart.SellerOrderRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de carrito.
 * Inyecta el mismo SellerCartRepository usado en SellerCategoryProductsViewModel,
 * por lo que ambos comparten la misma fuente de verdad y se mantienen sincronizados.
 */
class SellerCartViewModel(
    savedStateHandle: SavedStateHandle,
    private val cartRepository: SellerCartRepository,
    private val orderRepository: SellerOrderRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    // Para crear la orden necesitamos el customerId
    private val customerId: String = checkNotNull(savedStateHandle[CUSTOMER_ID_ARG])

    // Exponemos el flujo de items en carrito
    private val _cartItems = MutableStateFlow<List<SellerCartItem>>(emptyList())
    val cartItems: StateFlow<List<SellerCartItem>> = _cartItems.asStateFlow()

    // Calculamos el total a pagar en tiempo real
    val total: StateFlow<Double> = _cartItems
        .map { items -> items.sumOf { it.quantity * it.product.price.toDouble() } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    // Resultado de la petición de crear orden
    private val _confirmResult = MutableSharedFlow<Result<Unit>>(replay = 0)
    val confirmResult: SharedFlow<Result<Unit>> = _confirmResult.asSharedFlow()

    fun loadCartItems() {
        viewModelScope.launch(ioDispatcher) {
            cartRepository
                .getCartItemsWithDetails()
                .collectLatest { items ->
                    _cartItems.value = items
                }
        }
    }

    /**
     * Ajusta la cantidad de un item del carrito (o lo elimina si qty <= 0).
     */
    fun updateCartItem(productId: String, newQuantity: Int) {
        viewModelScope.launch(ioDispatcher) {
            when {
                newQuantity <= 0 -> cartRepository.removeItem(productId)
                else -> {
                    val existing = _cartItems.value.find { it.product.id == productId }
                    if (existing != null) {
                        cartRepository.updateItemQuantity(productId, newQuantity)
                    } else {
                        // obtener el SellerProductData a partir de items actuales
                        val dummy =
                            _cartItems.value.firstOrNull { it.product.id == productId }?.product
                                ?: return@launch
                        cartRepository.addItem(dummy, newQuantity)
                    }
                }
            }
        }
    }

    /**
     * Confirma el pedido: arma la lista y la envía al backend, limpia el carrito al éxito.
     * Emite el resultado (success/failure) vía confirmResult SharedFlow.
     */
    fun confirmOrder() {
        viewModelScope.launch(ioDispatcher) {
            val items = _cartItems.value
            val result = orderRepository.placeOrder(customerId, items)
            if (result.isSuccess) {
                cartRepository.clearCart()
            }
            _confirmResult.emit(result)
        }
    }
}

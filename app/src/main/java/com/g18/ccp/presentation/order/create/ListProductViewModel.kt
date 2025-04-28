package com.g18.ccp.presentation.order.create

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.core.constants.enums.OrderStatus
import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.data.local.model.cart.CartItem
import com.g18.ccp.data.remote.model.order.Order
import com.g18.ccp.data.remote.model.order.OrderItem
import com.g18.ccp.repository.order.OrdersRepository
import com.g18.ccp.repository.product.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private const val MAX_SUMMARY_LENGTH = 30
class ListProductViewModel(
    private val productRepository: ProductRepository,
    private val orderRepository: OrdersRepository,
) : ViewModel() {

    val uiState = mutableStateOf<Output<List<CartItem>>>(Output.Loading())
    private val _cart = mutableStateOf<List<CartItem>>(emptyList())
    val cart: State<List<CartItem>> get() = _cart
    private val _orderState = MutableStateFlow<Output<Unit>>(Output.Loading())
    val orderState: StateFlow<Output<Unit>> = _orderState

    fun loadProducts() {
        uiState.value = Output.Loading()

        viewModelScope.launch {
            try {
                val products = productRepository.getProducts().data
                val cartItems = products.map { CartItem(product = it) }
                uiState.value = Output.Success(cartItems)
            } catch (e: Exception) {
                uiState.value = Output.Failure(e)
            }
        }
    }

    fun addProduct(cartItem: CartItem) {
        val updatedCart = _cart.value.map {
            if (it.product.id == cartItem.product.id) {
                if (it.product.leftAmount >= it.quantity + 1) {
                    it.copy(quantity = it.quantity + 1)
                } else it
            } else it
        }.toMutableList()

        if (updatedCart.none { it.product.id == cartItem.product.id }) {
            updatedCart.add(CartItem(product = cartItem.product, quantity = 1))
        }

        _cart.value = updatedCart
        syncQuantitiesWithCart()
    }

    fun removeProduct(cartItem: CartItem) {
        val updatedCart = _cart.value.mapNotNull {
            if (it.product.id == cartItem.product.id) {
                if (it.quantity > 1) {
                    it.copy(quantity = it.quantity - 1)
                } else null
            } else it
        }

        _cart.value = updatedCart
        syncQuantitiesWithCart()
    }

    fun getQuantity(cartItem: CartItem): Int {
        return _cart.value.find { it.product.id == cartItem.product.id }?.quantity ?: 0
    }

    private fun syncQuantitiesWithCart() {
        val current = uiState.value
        if (current is Output.Success) {
            val updated = current.data.map { item ->
                val quantity = _cart.value.find { it.product.id == item.product.id }?.quantity ?: 0
                item.copy(quantity = quantity)
            }
            uiState.value = Output.Success(updated)
        }
    }

    fun removeAllProduct(cartItem: CartItem) {
        val currentCart = _cart.value.toMutableList()
        val existing = currentCart.find { it.product.id == cartItem.product.id }
        if (existing != null) {
            existing.quantity = 1
            currentCart.remove(existing)

            _cart.value = currentCart
            syncQuantitiesWithCart()
        }
    }

    fun createOrder() {
        viewModelScope.launch {
            _orderState.value = Output.Loading()
            try {
                val response = orderRepository.createOrder(order = buildOrderFromCart())
                if (response.isSuccessful) {
                    _orderState.value = Output.Success(Unit)
                } else {
                    _orderState.value = Output.Failure(Exception("Error creating order"))
                }
            } catch (e: Exception) {
                _orderState.value = Output.Failure(e)
            }
        }
    }

    fun clearOrderState() {
        _orderState.value = Output.Loading()
    }

    fun getOrderTotal(): Double {
        return _cart.value.sumOf { it.quantity * it.product.price.toDouble() }
    }

    private fun buildOrderFromCart(): Order {
        val summary = cart.value.joinToString(", ") { it.product.name }
            .let { if (it.length > MAX_SUMMARY_LENGTH) it.take(MAX_SUMMARY_LENGTH).plus("...") else it }

        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val total = getOrderTotal()

        val orderItems = cart.value.map {
            OrderItem(
                id = it.product.id,
                title = it.product.name,
                quantity = it.quantity,
                price = it.product.price,
                imageUrl = it.product.imageUrl
            )
        }

        return Order(
            id = UUID.randomUUID().toString(),
            summary = summary,
            date = date,
            total = total.toFloat(),
            status = OrderStatus.PREPARING,
            items = orderItems
        )
    }

}

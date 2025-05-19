package com.g18.ccp.presentation.seller.order.category.products.cart

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.core.constants.CUSTOMER_ID_ARG
import com.g18.ccp.data.local.model.cart.seller.SellerCartItem
import com.g18.ccp.data.remote.model.seller.order.product.SellerProductData
import com.g18.ccp.repository.seller.order.cart.SellerCartRepository
import com.g18.ccp.repository.seller.order.cart.SellerOrderRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SellerCartViewModelTest {

    // Rule to execute LiveData / coroutines on test dispatcher
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private lateinit var cartRepository: SellerCartRepository
    private lateinit var orderRepository: SellerOrderRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: SellerCartViewModel

    // Sample dummy product mock
    private val dummyProduct = mockk<SellerProductData>().apply {
        coEvery { id } returns "prod1"
        coEvery { price } returns 10.0f
    }

    // Sample cart item mock
    private val sampleCartItem = mockk<SellerCartItem>().apply {
        coEvery { product } returns dummyProduct
        coEvery { quantity } returns 2
    }

    @Before
    fun setup() {
        cartRepository = mockk()
        orderRepository = mockk()
        savedStateHandle = SavedStateHandle(mapOf(CUSTOMER_ID_ARG to "customer123"))
        viewModel = SellerCartViewModel(
            savedStateHandle,
            cartRepository,
            orderRepository,
            mainDispatcherRule.testDispatcher
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `given repository emits cart items when loadCartItems then cartItems and total updated`() =
        runTest {
            // Given
            coEvery { cartRepository.getCartItemsWithDetails() } returns flowOf(
                listOf(
                    sampleCartItem
                )
            )

            // When
            viewModel.loadCartItems()
            advanceUntilIdle()

            // Then
            assertEquals(listOf(sampleCartItem), viewModel.cartItems.value)
            assertEquals(2 * 10.0, viewModel.total.value, 0.0)
        }

    @Test
    fun `given existing cart item when updateCartItem with positive quantity then updateItemQuantity called`() =
        runTest {
            // Given
            coEvery { cartRepository.getCartItemsWithDetails() } returns flowOf(
                listOf(
                    sampleCartItem
                )
            )
            viewModel.loadCartItems()
            advanceUntilIdle()
            coEvery { cartRepository.updateItemQuantity("prod1", 3) } returns Result.success(Unit)

            // When
            viewModel.updateCartItem("prod1", 3)
            advanceUntilIdle()

            // Then
            coVerify(exactly = 1) { cartRepository.updateItemQuantity("prod1", 3) }
        }

    @Test
    fun `given existing cart item when updateCartItem with zero quantity then removeItem called`() =
        runTest {
            // Given
            coEvery { cartRepository.getCartItemsWithDetails() } returns flowOf(
                listOf(
                    sampleCartItem
                )
            )
            viewModel.loadCartItems()
            advanceUntilIdle()
            coEvery { cartRepository.removeItem("prod1") } returns Result.success(Unit)

            // When
            viewModel.updateCartItem("prod1", 0)
            advanceUntilIdle()

            // Then
            coVerify(exactly = 1) { cartRepository.removeItem("prod1") }
        }

    @Test
    fun `given successful placeOrder when confirmOrder then clearCart and emit success`() =
        runTest {
            // Given
            coEvery { cartRepository.getCartItemsWithDetails() } returns flowOf(
                listOf(
                    sampleCartItem
                )
            )
            viewModel.loadCartItems()
            advanceUntilIdle()
            coEvery {
                orderRepository.placeOrder(
                    "customer123",
                    any()
                )
            } returns Result.success(Unit)
            coEvery { cartRepository.clearCart() } returns Result.success(Unit)

            val emissions = mutableListOf<Result<Unit>>()
            val job = launch { viewModel.confirmResult.collect { emissions.add(it) } }

            // When
            viewModel.confirmOrder()
            advanceUntilIdle()

            // Then
            coVerifyOrder {
                orderRepository.placeOrder("customer123", listOf(sampleCartItem))
                cartRepository.clearCart()
            }
            assertTrue(emissions.first().isSuccess)

            job.cancel()
        }

    @Test
    fun `given failed placeOrder when confirmOrder then emit failure and not clearCart`() =
        runTest {
            // Given
            coEvery { cartRepository.getCartItemsWithDetails() } returns flowOf(
                listOf(
                    sampleCartItem
                )
            )
            viewModel.loadCartItems()
            advanceUntilIdle()
            val exception = RuntimeException("Network error")
            coEvery { orderRepository.placeOrder("customer123", any()) } returns Result.failure(
                exception
            )

            val emissions = mutableListOf<Result<Unit>>()
            val job = launch { viewModel.confirmResult.collect { emissions.add(it) } }

            // When
            viewModel.confirmOrder()
            advanceUntilIdle()

            // Then
            coVerify(exactly = 1) {
                orderRepository.placeOrder(
                    "customer123",
                    listOf(sampleCartItem)
                )
            }
            coVerify(exactly = 0) { cartRepository.clearCart() }
            assertTrue(emissions.first().isFailure)
            assertEquals(exception, emissions.first().exceptionOrNull())

            job.cancel()
        }
}

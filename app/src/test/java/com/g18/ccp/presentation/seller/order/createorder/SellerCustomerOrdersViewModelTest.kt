package com.g18.ccp.presentation.seller.order.createorder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.core.constants.CUSTOMER_ID_ARG
import com.g18.ccp.data.remote.model.seller.order.product.order.SellerCustomerOrderData
import com.g18.ccp.repository.seller.order.createorder.SellerCustomerOrdersRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SellerCustomerOrdersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private lateinit var repo: SellerCustomerOrdersRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: SellerCustomerOrdersViewModel

    private val sampleOrders = listOf(
        SellerCustomerOrderData(
            date = "2025-05-06",
            items = listOf(),
            orderId = "order123",
            status = "PREPARING",
            summary = "Summary",
            total = 1800.0
        )
    )

    @Before
    fun setup() {
        repo = mockk()
        savedStateHandle = SavedStateHandle(mapOf(CUSTOMER_ID_ARG to "cust1"))
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `loadOrders success updates state to Success`() = runTest {
        // Given
        coEvery { repo.fetchOrdersForCustomer("cust1") } returns Result.success(sampleOrders)
        viewModel = SellerCustomerOrdersViewModel(savedStateHandle, repo)
        // When
        advanceUntilIdle()
        // Then
        val state = viewModel.uiState.value
        assertTrue(state is SellerCustomerOrdersUiState.Success)
        assertEquals(sampleOrders, (state as SellerCustomerOrdersUiState.Success).orders)
    }

    @Test
    fun `loadOrders failure updates state to Error`() = runTest {
        // Given
        val ex = RuntimeException("Network down")
        coEvery { repo.fetchOrdersForCustomer("cust1") } returns Result.failure(ex)
        viewModel = SellerCustomerOrdersViewModel(savedStateHandle, repo)
        // When
        advanceUntilIdle()
        // Then
        val state = viewModel.uiState.value
        assertTrue(state is SellerCustomerOrdersUiState.Error)
        assertEquals("Network down", (state as SellerCustomerOrdersUiState.Error).message)
    }
}

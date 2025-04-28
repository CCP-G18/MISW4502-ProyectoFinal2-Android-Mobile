package com.g18.ccp.presentation.order

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.g18.ccp.core.constants.enums.OrderStatus
import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.data.remote.model.order.Order
import com.g18.ccp.data.remote.model.order.OrderItem
import com.g18.ccp.repository.order.OrdersRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersViewModelTest {

    private lateinit var repository: OrdersRepository
    private lateinit var viewModel: OrdersViewModel
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var scope: TestScope

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        repository = mockk()
        viewModel = OrdersViewModel(repository)
        Dispatchers.setMain(testDispatcher)
        scope = TestScope(testDispatcher)
    }

    @Test
    fun `given viewModel initialized then uiState is Loading`() {
        // Then
        assertTrue(viewModel.uiState.value is Output.Loading)
    }

    @Test
    fun `given repository returns success when loadOrders is called then uiState is Success`() =
        scope.runTest {
            // Given
            val fakeOrders = listOf(
                Order(
                    id = "1",
                    summary = "Papas, leche...",
                    date = "2025-05-01",
                    total = 125000.0f,
                    status = OrderStatus.PREPARING,
                    items = listOf(
                        OrderItem(
                            id = "1",
                            title = "Papas",
                            quantity = 2,
                            price = 3500.0f,
                            imageUrl = "https://example.com/papas.jpg"
                        )
                    )
                )
            )
            coEvery { repository.getOrders() } returns fakeOrders

            // When
            viewModel.loadOrders()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            assertTrue(viewModel.uiState.value is Output.Success)
            assertEquals(fakeOrders, (viewModel.uiState.value as Output.Success).data)
        }

    @Test
    fun `given repository throws exception when loadOrders is called then uiState is Failure`() =
        scope.runTest {
            // Given
            val exception = Exception("Network error")
            coEvery { repository.getOrders() } throws exception

            // When
            viewModel.loadOrders()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            assertTrue(viewModel.uiState.value is Output.Failure<*>)
            assertEquals(exception, (viewModel.uiState.value as Output.Failure<*>).exception)
        }

    @Test
    fun `given loadOrders is called then uiState is Loading before result`() = scope.runTest {
        // Given
        coEvery { repository.getOrders() } returns emptyList()

        // When
        viewModel.loadOrders()

        // Then
        assertTrue(viewModel.uiState.value is Output.Loading)

        testDispatcher.scheduler.advanceUntilIdle()
    }
}


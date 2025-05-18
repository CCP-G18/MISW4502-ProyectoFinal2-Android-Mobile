package com.g18.ccp.presentation.order.delivery


import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.core.constants.enums.OrderStatus
import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.data.remote.model.order.Order
import com.g18.ccp.repository.order.OrdersRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val testScope = TestScope(mainDispatcherRule.testDispatcher)

    private lateinit var viewModel: DeliveryViewModel
    private val repository = mockk<OrdersRepository>()

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `given repository returns mixed orders when loadOrders then uiState is Success with filtered list`() =
        testScope.runTest {
            // Given today's date and formatter matching ViewModel
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)
            val tomorrow = today.plusDays(1)

            // And sample orders
            val orderYesterdayPreparing = mockk<Order>()
            every { orderYesterdayPreparing.date } returns yesterday.format(formatter)
            every { orderYesterdayPreparing.status } returns OrderStatus.PREPARING

            val orderTodayOnRoute = mockk<Order>()
            every { orderTodayOnRoute.date } returns today.format(formatter)
            every { orderTodayOnRoute.status } returns OrderStatus.ON_ROUTE

            val orderTomorrowPreparing = mockk<Order>()
            every { orderTomorrowPreparing.date } returns tomorrow.format(formatter)
            every { orderTomorrowPreparing.status } returns OrderStatus.PREPARING

            // Repository returns all orders
            coEvery { repository.getOrders() } returns listOf(
                orderYesterdayPreparing,
                orderTodayOnRoute,
                orderTomorrowPreparing
            )

            viewModel = DeliveryViewModel(repository)

            // When loading orders
            viewModel.loadOrders()
            advanceUntilIdle()

            // Then uiState should be Success with only todayOnRoute and tomorrowPreparing
            val state = viewModel.uiState.value
            assertTrue(state is Output.Success)
            state as Output.Success<List<Order>>
            assertEquals(2, state.data.size)
            assertEquals(listOf(orderTodayOnRoute, orderTomorrowPreparing), state.data)
        }

    @Test
    fun `given repository throws when loadOrders then uiState is Failure`() = testScope.runTest {
        // Given repository throws exception
        val exception = RuntimeException("network error")
        coEvery { repository.getOrders() } throws exception

        viewModel = DeliveryViewModel(repository)

        // When loading orders
        viewModel.loadOrders()
        advanceUntilIdle()

        // Then uiState should be Failure with the thrown exception
        val state = viewModel.uiState.value
        assertTrue(state is Output.Failure<*>)
        state as Output.Failure<*>
        assertEquals(exception, state.exception)
    }
}

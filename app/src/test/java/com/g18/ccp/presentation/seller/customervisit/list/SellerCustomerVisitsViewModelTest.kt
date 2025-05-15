package com.g18.ccp.presentation.seller.customervisit.list

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.g18.ccp.core.constants.CUSTOMER_ID_ARG
import com.g18.ccp.data.remote.model.auth.UserInfo
import com.g18.ccp.data.remote.model.seller.visits.VisitData
import com.g18.ccp.repository.seller.CustomerRepository
import com.g18.ccp.repository.seller.customervisit.VisitRepository
import com.g18.ccp.repository.user.UserRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class SellerCustomerVisitsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var viewModel: SellerCustomerVisitsViewModel
    private val visitRepository = mockk<VisitRepository>()
    private val customerRepository = mockk<CustomerRepository>()
    private val userRepository = mockk<UserRepository>()
    private val savedStateHandle = SavedStateHandle(mapOf(CUSTOMER_ID_ARG to "cust1"))

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any<Throwable>()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
        unmockkStatic(Log::class)
    }

    @Test
    fun `given success responses when loadInitialData then uiState is Success with mapped visits`() =
        testScope.runTest {
            // Given a customer name
            coEvery { customerRepository.getCustomerById("cust1") } returns flowOf(mockk {
                every { name } returns "ClienteTeste"
            })
            // And a list of visits
            val date = Calendar.getInstance().apply { set(2025, 4, 15) }.time // May is month 4
            val visitData = VisitData(
                id = "v1",
                registerDate = date,
                observations = "Obs",
                customerId = "cust1",
                sellerId = "seller1"
            )
            coEvery { visitRepository.getVisitsForCustomer("cust1") } returns Result.success(
                listOf(
                    visitData
                )
            )
            // And user info
            val userInfo = UserInfo(
                id = "seller1",
                username = "user1",
                email = "user1@domain.com",
                role = "seller"
            )
            coEvery { userRepository.getUserInfoById("seller1") } returns userInfo

            viewModel = SellerCustomerVisitsViewModel(
                savedStateHandle,
                visitRepository,
                customerRepository,
                userRepository
            )

            // When loading initial data
            viewModel.loadInitialData()
            advanceUntilIdle()

            // Then uiState is Success
            val state = viewModel.uiState.value
            assertTrue(state is VisitsScreenUiState.Success)
            state as VisitsScreenUiState.Success
            assertEquals("ClienteTeste", state.customerName)
            assertEquals(1, state.visits.size)

            val item = state.visits.first()
            // Verify mapping
            assertEquals("v1", item.id)
            assertEquals(date, item.registerDate)
            assertEquals("Obs", item.observations)
            assertEquals("cust1", item.customerId)
            assertEquals("seller1", item.sellerId)
            assertEquals("user1", item.sellerName)
            assertEquals("user1@domain.com", item.sellerEmail)
            // displayDate formatted yyyy/MM/dd
            val expectedDisplay =
                java.text.SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(date)
            assertEquals(expectedDisplay, item.displayDate)
        }

    @Test
    fun `given failure visiting repository when loadInitialData then uiState is Success with empty visits`() =
        testScope.runTest {
            // Given customer fetch throws
            coEvery {
                customerRepository.getCustomerById(any())
            } throws RuntimeException("fail")
            // And visits failure
            coEvery { visitRepository.getVisitsForCustomer("cust1") } returns Result.failure(
                Exception("no visits")
            )

            viewModel = SellerCustomerVisitsViewModel(
                savedStateHandle,
                visitRepository,
                customerRepository,
                userRepository
            )

            // When loading
            viewModel.loadInitialData()
            advanceUntilIdle()

            // Then state is Success with default customer name and empty list
            val state = viewModel.uiState.value
            assertTrue(state is VisitsScreenUiState.Success)
            state as VisitsScreenUiState.Success
            assertEquals("Cliente", state.customerName)
            assertTrue(state.visits.isEmpty())
        }
}

package com.g18.ccp.presentation.seller.customerslist

import android.util.Log
import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.core.constants.enums.IdentificationType
import com.g18.ccp.data.remote.model.seller.CustomerData
import com.g18.ccp.repository.seller.CustomerRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
import java.io.IOException

@ExperimentalCoroutinesApi
class SellerCustomersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var customerRepository: CustomerRepository
    private lateinit var viewModel: SellerCustomersViewModel

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0

    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class) // <-- Limpia el mock DESPUÉS de los tests
    }

    @Test
    fun `given Repository Fetches And Refreshes Successfully - when ViewModel Initializes - then UiState Becomes Success With Data And Repository Called`() =
        runTest {
            val fakeCustomerList = listOf(TestData.customer("1"), TestData.customer("2"))
            val customerFlow = MutableStateFlow<List<CustomerData>>(emptyList())
            customerRepository = mockk {
                coEvery { getCustomers() } returns customerFlow
                coEvery { refreshCustomers() } coAnswers {
                    customerFlow.value = fakeCustomerList
                    Result.success(Unit)
                }
            }

            val collected = mutableListOf<CustomerListUiState>()
            val job = launch { viewModel.uiState.collect { collected.add(it) } }

            viewModel =
                SellerCustomersViewModel(customerRepository, mainDispatcherRule.testDispatcher)
            viewModel.start(true)
            advanceUntilIdle()
            job.cancel()

            assertTrue(collected.first() is CustomerListUiState.Loading)
            val state = collected.last()
            assertTrue(state is CustomerListUiState.Success)
            assertEquals(fakeCustomerList, (state as CustomerListUiState.Success).customers)
            assertEquals("", state.searchQuery)

            coVerify(exactly = 1) { customerRepository.getCustomers() }
            coVerify(exactly = 1) { customerRepository.refreshCustomers() }
        }

    @Test
    fun `given Repository Refresh Fails - when ViewModel Initializes - then UiState Becomes Error And Repository Called`() =
        runTest {
            val exception = IOException("Network Failed")
            customerRepository = mockk {
                coEvery { getCustomers() } returns flowOf(emptyList())
                coEvery { refreshCustomers() } returns Result.failure(exception)
            }

            val collected = mutableListOf<CustomerListUiState>()
            val job = launch { viewModel.uiState.collect { collected.add(it) } }

            viewModel =
                SellerCustomersViewModel(customerRepository, mainDispatcherRule.testDispatcher)
            viewModel.start(true)
            advanceUntilIdle()
            job.cancel()

            assertTrue(collected.first() is CustomerListUiState.Loading)
            val state = collected.last()
            assertTrue(state is CustomerListUiState.Error)
            assertEquals(
                "Fallo al refrescar: ${exception.message}",
                (state as CustomerListUiState.Error).message
            )
            assertEquals("", state.searchQuery)

            coVerify(exactly = 1) { customerRepository.getCustomers() }
            coVerify(exactly = 1) { customerRepository.refreshCustomers() }
        }

    @Test
    fun `when onSearchQueryChanged is Called - then searchQuery StateFlow Updates`() = runTest {
        customerRepository = mockk {
            coEvery { getCustomers() } returns flow {
                emit(
                    listOf(
                        TestData.customer("1"),
                        TestData.customer("2")
                    )
                )
            }
            coEvery { refreshCustomers() } returns Result.success(Unit)
        }
        viewModel = SellerCustomersViewModel(customerRepository, mainDispatcherRule.testDispatcher)
        viewModel.start(true)
        val testQuery = "new search"
        val collected = mutableListOf<String>()
        val collectedUiState = mutableListOf<CustomerListUiState>()
        val job = launch {
            viewModel.searchQuery.collect { collected.add(it) }
        }
        val jobUiState = launch {
            viewModel.uiState.collect { collectedUiState.add(it) }
        }
        advanceUntilIdle()

        viewModel.onSearchQueryChanged(testQuery)
        advanceUntilIdle()
        job.cancel()
        jobUiState.cancel()

        assertEquals("", collected.first())
        assertEquals(testQuery, collected.last())
        assertEquals(testQuery, viewModel.searchQuery.value)
    }


    @Test
    fun `given UiState is Loading or Error - when searchQuery Changes - then filteredCustomers is Empty`() =
        runTest {
            customerRepository = mockk {
                coEvery { getCustomers() } returns emptyFlow()
                coEvery { refreshCustomers() } returns Result.failure(IOException("Failed"))
            }

            viewModel =
                SellerCustomersViewModel(customerRepository, mainDispatcherRule.testDispatcher)
            viewModel.start(true)
            viewModel.uiState.first { it is CustomerListUiState.Error }
            advanceUntilIdle()

            val collectedFilteredValues = mutableListOf<List<CustomerData>>()
            val filterJob =
                launch { viewModel.filteredCustomers.collect { collectedFilteredValues.add(it) } }
            advanceUntilIdle()

            assertEquals(emptyList<CustomerData>(), collectedFilteredValues.last())

            viewModel.onSearchQueryChanged("test")
            advanceUntilIdle()

            assertEquals(emptyList<CustomerData>(), collectedFilteredValues.last())

            filterJob.cancel()
        }


}

private object TestData {
    fun customer(
        id: String,
        name: String = "Test Name",
        identificationNumber: String = "987654",
        address: String = "Address $id"
    ) = CustomerData(
        address = address,
        city = "City $id",
        country = "Country",
        email = "email$id@example.com",
        id = id,
        identificationNumber = identificationNumber,
        identificationType = IdentificationType.CC,
        name = name
    )
}

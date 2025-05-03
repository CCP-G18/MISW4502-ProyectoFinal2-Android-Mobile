package com.g18.ccp.presentation.seller.personalinfo

import androidx.lifecycle.SavedStateHandle
import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.core.constants.CUSTOMER_ID_ARG
import com.g18.ccp.core.constants.enums.IdentificationType
import com.g18.ccp.data.remote.model.seller.CustomerData
import com.g18.ccp.repository.seller.CustomerRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class SellerCustomerPersonalInfoViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var customerRepository: CustomerRepository

    private lateinit var viewModel: SellerCustomerPersonalInfoViewModel

    private val testCustomerId = "test-id-123"

    @Test(expected = IllegalStateException::class)
    fun `given SavedStateHandle Without CustomerId - when ViewModel Initializes - then Throws IllegalStateException`() {
        val savedStateHandle = SavedStateHandle()
        viewModel = SellerCustomerPersonalInfoViewModel(savedStateHandle, customerRepository)
    }

    @Test
    fun `given Repository Returns Customer - when ViewModel Initializes - then UiState Is Success`() =
        runTest {
            val fakeCustomer = TestData.customer(testCustomerId)
            val customerFlow = flowOf(fakeCustomer)
            val savedStateHandle = SavedStateHandle(mapOf(CUSTOMER_ID_ARG to testCustomerId))
            every { customerRepository.getCustomerById(testCustomerId) } returns customerFlow

            val collectedStates = mutableListOf<CustomerInfoUiState>()
            val job = launch { viewModel.uiState.collect { collectedStates.add(it) } }

            viewModel = SellerCustomerPersonalInfoViewModel(savedStateHandle, customerRepository)
            advanceUntilIdle()
            job.cancel()

            assertTrue(collectedStates.first() is CustomerInfoUiState.Loading)
            val lastState = collectedStates.last()
            assertTrue(lastState is CustomerInfoUiState.Success)
            assertEquals(fakeCustomer, (lastState as CustomerInfoUiState.Success).customer)
            verify(exactly = 1) { customerRepository.getCustomerById(testCustomerId) }
        }

    @Test
    fun `given Repository Returns Null - when ViewModel Initializes - then UiState Is NotFound`() =
        runTest {
            val customerFlow = flowOf<CustomerData?>(null)
            val savedStateHandle = SavedStateHandle(mapOf(CUSTOMER_ID_ARG to testCustomerId))
            every { customerRepository.getCustomerById(testCustomerId) } returns customerFlow

            val collectedStates = mutableListOf<CustomerInfoUiState>()
            val job = launch { viewModel.uiState.collect { collectedStates.add(it) } }

            viewModel = SellerCustomerPersonalInfoViewModel(savedStateHandle, customerRepository)
            advanceUntilIdle()
            job.cancel()

            assertTrue(collectedStates.first() is CustomerInfoUiState.Loading)
            assertTrue(collectedStates.last() is CustomerInfoUiState.NotFound)
            verify(exactly = 1) { customerRepository.getCustomerById(testCustomerId) }
        }

    @Test
    fun `given Repository Flow Throws Exception - when ViewModel Initializes - then UiState Is Error`() =
        runTest {
            val expectedException = IOException("Database error")
            val errorFlow = flow<CustomerData?> { throw expectedException }
            val savedStateHandle = SavedStateHandle(mapOf(CUSTOMER_ID_ARG to testCustomerId))
            every { customerRepository.getCustomerById(testCustomerId) } returns errorFlow

            val collectedStates = mutableListOf<CustomerInfoUiState>()
            val job = launch { viewModel.uiState.collect { collectedStates.add(it) } }

            viewModel = SellerCustomerPersonalInfoViewModel(savedStateHandle, customerRepository)
            advanceUntilIdle()
            job.cancel()

            assertTrue(collectedStates.first() is CustomerInfoUiState.Loading)
            val lastState = collectedStates.last()
            assertTrue(lastState is CustomerInfoUiState.Error)
            assertEquals(
                "Error al cargar información: ${expectedException.message}",
                (lastState as CustomerInfoUiState.Error).message
            )
            verify(exactly = 1) { customerRepository.getCustomerById(testCustomerId) }
        }
}

private object TestData {
    fun customer(id: String) = CustomerData(
        address = "Test Address 123",
        city = "Test City",
        country = "Test Country",
        email = "test@example.com",
        id = id,
        identificationNumber = "123456789",
        identificationType = IdentificationType.CC,
        name = "Test Customer Name"
    )
}

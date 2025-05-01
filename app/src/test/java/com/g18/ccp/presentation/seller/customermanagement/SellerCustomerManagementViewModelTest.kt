package com.g18.ccp.presentation.seller.customermanagement

import androidx.lifecycle.SavedStateHandle
import com.g18.ccp.core.constants.CUSTOMER_ID_ARG
import com.g18.ccp.core.constants.enums.IdentificationType
import com.g18.ccp.data.remote.model.seller.CustomerData
import com.g18.ccp.repository.seller.CustomerRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import java.io.IOException

@ExperimentalCoroutinesApi
class SellerCustomerManagementViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val customerId = "cust123"
    private lateinit var repository: CustomerRepository

    @Test(expected = IllegalStateException::class)
    fun givenNoCustomerIdInSavedState_whenInit_thenThrows() {
        // given
        val handle = SavedStateHandle(mapOf<String, Any>())
        repository = mockk()

        // when
        SellerCustomerManagementViewModel(handle, repository)

        // then exception
    }

    @Test
    fun givenCustomerExists_whenInit_thenUiStateIsSuccessAndRepositoryCalled() = runTest {
        // given
        val customer = TestData.customer(id = customerId)
        val handle = SavedStateHandle(mapOf(CUSTOMER_ID_ARG to customerId))
        repository = mockk {
            coEvery { getCustomerById(customerId) } returns flowOf(customer)
        }

        // when
        val viewModel = SellerCustomerManagementViewModel(handle, repository)
        val collected = mutableListOf<CustomerManagementUiState>()
        val job = launch {
            viewModel.uiState.collect { collected.add(it) }
        }
        advanceUntilIdle()
        job.cancel()

        // then state should be Success
        val state = collected.last()
        assert(state is CustomerManagementUiState.Success)
        val found = (state as CustomerManagementUiState.Success).customer
        Assert.assertEquals(customer, found)

        // and repository was invoked
        coVerify(exactly = 1) { repository.getCustomerById(customerId) }
    }

    @Test
    fun givenCustomerNotFound_whenInit_thenUiStateIsNotFoundAndRepositoryCalled() = runTest {
        // given
        val handle = SavedStateHandle(mapOf(CUSTOMER_ID_ARG to customerId))
        repository = mockk {
            coEvery { getCustomerById(customerId) } returns flowOf(null)
        }

        // when
        val viewModel = SellerCustomerManagementViewModel(handle, repository)
        val collected = mutableListOf<CustomerManagementUiState>()
        val job = launch {
            viewModel.uiState.collect { collected.add(it) }
        }
        advanceUntilIdle()
        job.cancel()

        // then state should be NotFound
        val state = collected.last()
        Assert.assertTrue(state is CustomerManagementUiState.NotFound)

        // and repository was invoked
        coVerify(exactly = 1) { repository.getCustomerById(customerId) }
    }

    @Test
    fun givenRepositoryError_whenInit_thenUiStateIsErrorAndRepositoryCalled() = runTest {
        // given
        val handle = SavedStateHandle(mapOf(CUSTOMER_ID_ARG to customerId))
        val exception = IOException("DB failure")
        repository = mockk {
            coEvery { getCustomerById(customerId) } returns flow { throw exception }
        }

        // when
        val viewModel = SellerCustomerManagementViewModel(handle, repository)
        val collected = mutableListOf<CustomerManagementUiState>()
        val job = launch {
            viewModel.uiState.collect { collected.add(it) }
        }
        advanceUntilIdle()
        job.cancel()

        // then state should be Error
        val state = collected.last()
        assert(state is CustomerManagementUiState.Error)
        val msg = (state as CustomerManagementUiState.Error).message
        Assert.assertTrue(msg.contains("Error al cargar cliente:"))
        Assert.assertTrue(msg.contains("DB failure"))

        // and repository was invoked
        coVerify(exactly = 1) { repository.getCustomerById(customerId) }
    }
}

// Helper para crear datos de prueba
private object TestData {
    fun customer(id: String) = CustomerData(
        address = "Address",
        city = "City",
        country = "Country",
        email = "email@example.com",
        id = id,
        identificationNumber = "123456789",
        identificationType = IdentificationType.CC,
        name = "Test Name"
    )
}

// MainDispatcherRule para coroutines
@ExperimentalCoroutinesApi
class MainDispatcherRule : TestWatcher() {
    private val dispatcher = UnconfinedTestDispatcher()
    override fun starting(description: org.junit.runner.Description?) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: org.junit.runner.Description?) {
        Dispatchers.resetMain()
    }
}

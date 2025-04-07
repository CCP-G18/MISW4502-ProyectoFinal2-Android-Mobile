package com.g18.ccp.presentation.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.g18.ccp.core.utils.auth.UiState
import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.repository.auth.register.client.ClientRegisterRepository
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
class RegisterClientViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: ClientRegisterRepository
    private lateinit var viewModel: RegisterClientViewModel
    private lateinit var scope: TestScope

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        repository = mockk()
        viewModel = RegisterClientViewModel(repository)
        Dispatchers.setMain(testDispatcher)
        scope = TestScope(testDispatcher)
    }

    @Test
    fun `given all fields are valid when registerClient is called then uiState is Success`() =
        scope.runTest {
            // Given
            with(viewModel) {
                onNameChange("Ana")
                onLastNameChange("Villanueva")
                onTypeIdChange("CC")
                onNumIdChange("123456789")
                onEmailChange("ana@example.com")
                onPasswordChange("123456")
                onConfirmPasswordChange("123456")
                onCountryChange("Colombia")
                onCityChange("Medellín")
                onAddressChange("Calle falsa 123")
            }
            coEvery {
                repository.registerClient(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns Output.Success(mockk())

            // When
            viewModel.registerClient()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            assertEquals(UiState.Success, viewModel.uiState.value)
        }

    @Test
    fun `given invalid email when registerClient is called then uiState remains Idle`() =
        scope.runTest {
            // Given
            with(viewModel) {
                onNameChange("Ana")
                onLastNameChange("Villanueva")
                onTypeIdChange("CC")
                onNumIdChange("123456789")
                onEmailChange("ana")
                onPasswordChange("123456")
                onConfirmPasswordChange("123456")
                onCountryChange("Colombia")
                onCityChange("Medellín")
                onAddressChange("Calle falsa 123")
            }

            // When
            viewModel.registerClient()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            assertEquals(UiState.Idle, viewModel.uiState.value)
        }

    @Test
    fun `given repository returns failure when registerClient is called then uiState is Error`() =
        scope.runTest {
            // Given
            with(viewModel) {
                onNameChange("Ana")
                onLastNameChange("Villanueva")
                onTypeIdChange("CC")
                onNumIdChange("123456789")
                onEmailChange("ana@example.com")
                onPasswordChange("123456")
                onConfirmPasswordChange("123456")
                onCountryChange("Colombia")
                onCityChange("Medellín")
                onAddressChange("Calle falsa 123")
            }
            coEvery {
                repository.registerClient(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns Output.Failure(Exception("Error"))

            // When
            viewModel.registerClient()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            assertTrue(viewModel.uiState.value is UiState.Error)
        }
}

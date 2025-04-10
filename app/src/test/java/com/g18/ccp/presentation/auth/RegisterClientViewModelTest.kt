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

    @Test
    fun `given empty name when onNameChange then nameError is true`() {
        // When
        viewModel.onNameChange("")

        // Then
        assertTrue(viewModel.nameError.value)
    }

    @Test
    fun `given blank last name when onLastNameChange then lastNameError is true`() {
        // When
        viewModel.onLastNameChange(" ")

        // Then
        assertTrue(viewModel.lastNameError.value)
    }

    @Test
    fun `given empty typeId when onTypeIdChange then typeIdError is true`() {
        // When
        viewModel.onTypeIdChange("")

        // Then
        assertTrue(viewModel.typeIdError.value)
    }

    @Test
    fun `given invalid numId when onNumIdChange then numIdError is true`() {
        // When
        viewModel.onNumIdChange("abc123")

        // Then
        assertTrue(viewModel.numIdError.value)
    }

    @Test
    fun `given invalid email when onEmailChange then emailError is true`() {
        // When
        viewModel.onEmailChange("not-an-email")

        // Then
        assertTrue(viewModel.emailError.value)
    }

    @Test
    fun `given short password when onPasswordChange then passwordError is true`() {
        // When
        viewModel.onPasswordChange("123")

        // Then
        assertTrue(viewModel.passwordError.value)
    }

    @Test
    fun `given mismatched confirmPassword when onConfirmPasswordChange then confirmPasswordError is true`() {
        // Given
        viewModel.onPasswordChange("123456")

        // When
        viewModel.onConfirmPasswordChange("654321")

        // Then
        assertTrue(viewModel.confirmPasswordError.value)
    }

    @Test
    fun `given blank country when onCountryChange then countryError is true`() {
        // When
        viewModel.onCountryChange("")

        // Then
        assertTrue(viewModel.countryError.value)
    }

    @Test
    fun `given blank city when onCityChange then cityError is true`() {
        // When
        viewModel.onCityChange("")

        // Then
        assertTrue(viewModel.cityError.value)
    }

    @Test
    fun `given blank address when onAddressChange then addressError is true`() {
        // When
        viewModel.onAddressChange("")

        // Then
        assertTrue(viewModel.addressError.value)
    }

    @Test
    fun `given resetRegisterClientState called then uiState is Idle`() {
        // Given
        viewModel.uiState.value = UiState.Success

        // When
        viewModel.resetRegisterClientState()

        // Then
        assertEquals(UiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `given all fields valid when dataIsValid then returns true`() {
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

        // When
        val result = viewModel.dataIsValid()

        // Then
        assertTrue(result)
    }

    @Test
    fun `given invalid name when dataIsValid then returns false`() {
        fillValidData()
        viewModel.onNameChange("")
        assertTrue(viewModel.nameError.value)
        assertEquals(false, viewModel.dataIsValid())
    }

    @Test
    fun `given invalid last name when dataIsValid then returns false`() {
        fillValidData()
        viewModel.onLastNameChange("")
        assertTrue(viewModel.lastNameError.value)
        assertEquals(false, viewModel.dataIsValid())
    }

    @Test
    fun `given invalid typeId when dataIsValid then returns false`() {
        fillValidData()
        viewModel.onTypeIdChange("")
        assertTrue(viewModel.typeIdError.value)
        assertEquals(false, viewModel.dataIsValid())
    }

    @Test
    fun `given invalid numId when dataIsValid then returns false`() {
        fillValidData()
        viewModel.onNumIdChange("abc")
        assertTrue(viewModel.numIdError.value)
        assertEquals(false, viewModel.dataIsValid())
    }

    @Test
    fun `given invalid email when dataIsValid then returns false`() {
        fillValidData()
        viewModel.onEmailChange("invalid")
        assertTrue(viewModel.emailError.value)
        assertEquals(false, viewModel.dataIsValid())
    }

    @Test
    fun `given invalid password when dataIsValid then returns false`() {
        fillValidData()
        viewModel.onPasswordChange("123")
        assertTrue(viewModel.passwordError.value)
        assertEquals(false, viewModel.dataIsValid())
    }

    @Test
    fun `given mismatched confirmPassword when dataIsValid then returns false`() {
        fillValidData()
        viewModel.onConfirmPasswordChange("different")
        assertTrue(viewModel.confirmPasswordError.value)
        assertEquals(false, viewModel.dataIsValid())
    }

    @Test
    fun `given invalid country when dataIsValid then returns false`() {
        fillValidData()
        viewModel.onCountryChange("")
        assertTrue(viewModel.countryError.value)
        assertEquals(false, viewModel.dataIsValid())
    }

    @Test
    fun `given invalid city when dataIsValid then returns false`() {
        fillValidData()
        viewModel.onCityChange("")
        assertTrue(viewModel.cityError.value)
        assertEquals(false, viewModel.dataIsValid())
    }

    @Test
    fun `given invalid address when dataIsValid then returns false`() {
        fillValidData()
        viewModel.onAddressChange("")
        assertTrue(viewModel.addressError.value)
        assertEquals(false, viewModel.dataIsValid())
    }

    private fun fillValidData() {
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
            onAddressChange("Calle 123")
        }
    }
}

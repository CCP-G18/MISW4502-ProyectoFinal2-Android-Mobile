package com.g18.ccp.presentation.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.g18.ccp.core.utils.auth.LoginUiState
import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.repository.auth.LoginRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val loginRepository: LoginRepository = mockk()
    private lateinit var viewModel: LoginViewModel
    private val dispatcher = StandardTestDispatcher(TestCoroutineScheduler())

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = LoginViewModel(loginRepository)
    }

    @Test
    fun givenValidEmail_whenOnEmailChange_thenIsEmailValidIsTrue() {
        viewModel.onEmailChange("test@email.com")
        assertTrue(viewModel.isEmailValid.value)
        assertFalse(viewModel.dataIsValid())
    }

    @Test
    fun givenInvalidEmail_whenOnEmailChange_thenIsEmailValidIsFalse() {
        viewModel.onEmailChange("invalid")
        assertFalse(viewModel.isEmailValid.value)
        assertFalse(viewModel.dataIsValid())
    }

    @Test
    fun givenValidPassword_whenOnPasswordChange_thenIsPasswordValidIsTrue() {
        viewModel.onPasswordChange("123456")
        assertTrue(viewModel.isPasswordValid.value)
        assertFalse(viewModel.dataIsValid())
    }

    @Test
    fun givenInvalidPassword_whenOnPasswordChange_thenIsPasswordValidIsFalse() {
        viewModel.onPasswordChange("123")
        assertFalse(viewModel.isPasswordValid.value)
        assertFalse(viewModel.dataIsValid())
    }

    @Test
    fun givenValidData_whenValidateAndLogin_thenUiStateIsSuccess() = runTest(dispatcher) {
        // Given
        viewModel.onEmailChange("test@email.com")
        viewModel.onPasswordChange("123456")
        coEvery { loginRepository.login(any(), any()) } returns Output.Success(Unit)

        // When
        viewModel.validateAndLogin()
        dispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(LoginUiState.Success, viewModel.uiState.value)
        assertTrue(viewModel.dataIsValid())
    }

    @Test
    fun givenLoginFails_whenValidateAndLogin_thenUiStateIsError() = runTest(dispatcher) {
        // Given
        viewModel.onEmailChange("test@email.com")
        viewModel.onPasswordChange("123456")
        val exception = Exception("Login failed")
        coEvery { loginRepository.login(any(), any()) } returns Output.Failure(exception)

        // When
        viewModel.validateAndLogin()
        dispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is LoginUiState.Error)
        assertEquals(exception, (state as LoginUiState.Error).exception)
    }

    @Test
    fun givenResetCalled_whenResetLoginState_thenUiStateIsIdle() {
        // Given
        viewModel.uiState.value = LoginUiState.Success

        // When
        viewModel.resetLoginState()

        // Then
        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }
}

package com.g18.ccp.presentation.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.core.utils.auth.UiState
import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.core.utils.validation.Validator
import com.g18.ccp.repository.auth.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val _email = mutableStateOf("vendedor.ccp@example.com")
    val email: State<String> get() = _email

    private val _isEmailValid = mutableStateOf(false)
    val isEmailValid: State<Boolean> get() = _isEmailValid

    private val _password = mutableStateOf("vendedorcpp\$1")
    val password: State<String> get() = _password

    private val _isPasswordValid = mutableStateOf(false)
    val isPasswordValid: State<Boolean> get() = _isPasswordValid

    var uiState = mutableStateOf<UiState>(UiState.Idle)
        private set

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _isEmailValid.value = Validator.isEmailValid(newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _isPasswordValid.value = Validator.isPasswordValid(newPassword)
    }

    fun dataIsValid(): Boolean {
        return _isEmailValid.value && _isPasswordValid.value
    }

    fun validateAndLogin() {
        viewModelScope.launch {
            if (_isEmailValid.value && _isPasswordValid.value) {
                uiState.value = UiState.Loading
                val result = loginRepository.login(_email.value, _password.value)
                uiState.value = when (result) {
                    is Output.Success -> UiState.Success
                    is Output.Failure<*> -> UiState.Error(
                        result.exception as? Exception
                    )

                    is Output.Loading -> UiState.Loading
                }
            }
        }
    }

    fun resetLoginState() {
        uiState.value = UiState.Idle
    }

    suspend fun getUserRole(): String = loginRepository.getUserRole()
}

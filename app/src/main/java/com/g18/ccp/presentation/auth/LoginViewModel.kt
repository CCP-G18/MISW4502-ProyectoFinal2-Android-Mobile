package com.g18.ccp.presentation.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.core.utils.auth.LoginUiState
import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.core.utils.validation.Validator
import com.g18.ccp.repository.auth.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {
    private val _email = mutableStateOf("")
    val email: State<String> get() = _email

    private val _isEmailValid = mutableStateOf(false)
    val isEmailValid: State<Boolean> get() = _isEmailValid

    private val _password = mutableStateOf("")
    val password: State<String> get() = _password

    private val _isPasswordValid = mutableStateOf(false)
    val isPasswordValid: State<Boolean> get() = _isPasswordValid

    var uiState = mutableStateOf<LoginUiState>(LoginUiState.Idle)
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
                uiState.value = LoginUiState.Loading
                val result = loginRepository.login(_email.value, _password.value)
                uiState.value = when (result) {
                    is Output.Success -> LoginUiState.Success
                    is Output.Failure<*> -> LoginUiState.Error(
                        result.exception as? Exception
                    )

                    is Output.Loading -> LoginUiState.Loading
                }
            }
        }
    }

    fun resetLoginState() {
        uiState.value = LoginUiState.Idle
    }
}

package com.g18.ccp.presentation.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.core.utils.auth.UiState
import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.core.utils.validation.Validator
import com.g18.ccp.repository.auth.register.client.ClientRegisterRepository
import kotlinx.coroutines.launch

class RegisterClientViewModel(
    private val registerClientRepository: ClientRegisterRepository
) : ViewModel() {

    private val _name = mutableStateOf("")
    val name: State<String> get() = _name
    val nameError = mutableStateOf(false)

    private val _lastName = mutableStateOf("")
    val lastName: State<String> get() = _lastName
    val lastNameError = mutableStateOf(false)

    private val _typeId = mutableStateOf("")
    val typeId: State<String> get() = _typeId
    val typeIdError = mutableStateOf(false)

    private val _numId = mutableStateOf("")
    val numId: State<String> get() = _numId
    val numIdError = mutableStateOf(false)

    private val _email = mutableStateOf("")
    val email: State<String> get() = _email
    val emailError = mutableStateOf(false)

    private val _password = mutableStateOf("")
    val password: State<String> get() = _password
    val passwordError = mutableStateOf(false)

    private val _confirmPassword = mutableStateOf("")
    val confirmPassword: State<String> get() = _confirmPassword
    val confirmPasswordError = mutableStateOf(false)

    private val _country = mutableStateOf("")
    val country: State<String> get() = _country
    val countryError = mutableStateOf(false)

    private val _city = mutableStateOf("")
    val city: State<String> get() = _city
    val cityError = mutableStateOf(false)

    private val _address = mutableStateOf("")
    val address: State<String> get() = _address
    val addressError = mutableStateOf(false)

    var uiState = mutableStateOf<UiState>(UiState.Idle)
        private set

    fun onNameChange(value: String) {
        _name.value = value
        nameError.value = value.isBlank()
    }

    fun onLastNameChange(value: String) {
        _lastName.value = value
        lastNameError.value = value.isBlank()
    }

    fun onTypeIdChange(value: String) {
        _typeId.value = value
        typeIdError.value = value.isBlank()
    }

    fun onNumIdChange(value: String) {
        _numId.value = value
        numIdError.value = value.isBlank() || !value.all { it.isDigit() }
    }

    fun onEmailChange(value: String) {
        _email.value = value
        emailError.value = !Validator.isEmailValid(value)
    }

    fun onPasswordChange(value: String) {
        _password.value = value
        passwordError.value = !Validator.isPasswordValid(value)
    }

    fun onConfirmPasswordChange(value: String) {
        _confirmPassword.value = value
        confirmPasswordError.value = value != _password.value
    }

    fun onCountryChange(value: String) {
        _country.value = value
        countryError.value = value.isBlank()
    }

    fun onCityChange(value: String) {
        _city.value = value
        cityError.value = value.isBlank()
    }

    fun onAddressChange(value: String) {
        _address.value = value
        addressError.value = value.isBlank()
    }

    fun dataIsValid(): Boolean {
        return !nameError.value &&
                !lastNameError.value &&
                !typeIdError.value &&
                !numIdError.value &&
                !emailError.value &&
                !passwordError.value &&
                !confirmPasswordError.value &&
                !countryError.value &&
                !cityError.value &&
                !addressError.value
    }

    fun registerClient() {
        viewModelScope.launch {
            if (dataIsValid()) {
                val result = registerClientRepository.registerClient(
                    name = _name.value,
                    lastname = _lastName.value,
                    identificationType = _typeId.value,
                    identificationNumber = _numId.value.toLongOrNull() ?: 0L,
                    country = _country.value,
                    city = _city.value,
                    address = _address.value,
                    email = _email.value,
                    password = _password.value,
                )
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

    fun resetRegisterClientState() {
        uiState.value = UiState.Idle
    }
}

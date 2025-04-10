package com.g18.ccp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.g18.ccp.R
import com.g18.ccp.core.constants.enums.IdentificationType
import com.g18.ccp.core.constants.enums.getDisplayName
import com.g18.ccp.core.utils.auth.UiState
import com.g18.ccp.core.utils.error.getErrorMessage
import com.g18.ccp.presentation.auth.RegisterClientViewModel
import com.g18.ccp.ui.theme.BackgroundColor
import com.g18.ccp.ui.theme.BlackColor
import com.g18.ccp.ui.theme.LightGray
import com.g18.ccp.ui.theme.MainColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterClientScreen(
    viewModel: RegisterClientViewModel,
    onBackClick: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val registerState by viewModel.uiState
    val idTypeEnums = IdentificationType.entries

    val idTypeOptions = idTypeEnums.map { it.getDisplayName(context) }

    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }


    LaunchedEffect(registerState) {
        when (registerState) {
            is UiState.Error -> {

            }

            is UiState.Success -> {
                onRegisterSuccess()
                viewModel.resetRegisterClientState()
            }

            else -> {
            }
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        text = stringResource(R.string.app_name),
                        color = BackgroundColor
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("register_back_button"),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = BackgroundColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MainColor,
                    titleContentColor = BackgroundColor,
                    navigationIconContentColor = BackgroundColor
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightGray
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.register_client_title),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MainColor
                    )
                    RegisterTextField(
                        viewModel.name.value,
                        viewModel::onNameChange,
                        stringResource(R.string.name_label),
                        viewModel.nameError.value,
                        stringResource(R.string.register_error_name)
                    )
                    RegisterTextField(
                        viewModel.lastName.value,
                        viewModel::onLastNameChange,
                        stringResource(R.string.last_name_label),
                        viewModel.lastNameError.value,
                        stringResource(R.string.register_error_last_name)
                    )
                    RegisterDropdownField(
                        value = viewModel.typeId.value,
                        onValueChange = viewModel::onTypeIdChange,
                        label = stringResource(R.string.id_type_label),
                        isError = viewModel.typeIdError.value,
                        errorMessage = stringResource(R.string.register_error_type_id),
                        options = idTypeOptions
                    )
                    RegisterTextField(
                        viewModel.numId.value,
                        viewModel::onNumIdChange,
                        stringResource(R.string.id_number_label),
                        viewModel.numIdError.value,
                        stringResource(R.string.register_error_num_id),
                        keyboardType = KeyboardType.Number
                    )
                    RegisterTextField(
                        viewModel.email.value,
                        viewModel::onEmailChange,
                        stringResource(R.string.email_label),
                        viewModel.emailError.value,
                        stringResource(R.string.register_error_email)
                    )
                    RegisterTextField(
                        value = viewModel.password.value,
                        onValueChange = viewModel::onPasswordChange,
                        label = stringResource(R.string.password_label),
                        isError = viewModel.passwordError.value,
                        errorMessage = stringResource(R.string.register_error_password),
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon =
                                if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = BlackColor
                                )
                            }
                        }
                    )

                    RegisterTextField(
                        value = viewModel.confirmPassword.value,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        label = stringResource(R.string.confirm_password_label),
                        isError = viewModel.confirmPasswordError.value,
                        errorMessage = stringResource(R.string.register_error_confirm_password),
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon =
                                if (showConfirmPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = if (showConfirmPassword) "Ocultar confirmación" else "Mostrar confirmación",
                                    tint = BlackColor
                                )
                            }
                        }
                    )

                    RegisterTextField(
                        viewModel.country.value,
                        viewModel::onCountryChange,
                        stringResource(R.string.country_label),
                        viewModel.countryError.value,
                        stringResource(R.string.register_error_country)
                    )
                    RegisterTextField(
                        viewModel.city.value,
                        viewModel::onCityChange,
                        stringResource(R.string.city_label),
                        viewModel.cityError.value,
                        stringResource(R.string.register_error_city)
                    )
                    RegisterTextField(
                        viewModel.address.value,
                        viewModel::onAddressChange,
                        stringResource(R.string.address_label),
                        viewModel.addressError.value,
                        stringResource(R.string.register_error_address)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (registerState) {
                is UiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 36.dp)
                        .height(48.dp)
                )

                is UiState.Error -> Text(
                    modifier = Modifier.height(48.dp),
                    text = (registerState as UiState.Error).exception.getErrorMessage(context),
                    color = MaterialTheme.colorScheme.error
                )

                else -> {
                    // Do nothing
                }
            }

            Button(
                onClick = {
                    viewModel.registerClient()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainColor,
                    contentColor = BackgroundColor
                ),
                enabled = viewModel.dataIsValid()
            ) {
                Text(stringResource(R.string.register_button_text))
            }
        }
    }
}

@Composable
fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isError) MaterialTheme.colorScheme.error else MainColor,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .background(
                    if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else BackgroundColor,
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .fillMaxWidth()
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                visualTransformation = visualTransformation,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = BlackColor),
                decorationBox = { innerTextField ->
                    Row(
                        Modifier.height(36.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.weight(1f)) {
                            innerTextField()
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        trailingIcon?.let { it() }
                    }
                }
            )
        }

        if (isError && !errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterDropdownField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorMessage: String? = null,
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isError) MaterialTheme.colorScheme.error else MainColor,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            Box(
                modifier = Modifier
                    .background(
                        if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else BackgroundColor,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .fillMaxWidth()
                    .clickable { expanded = true } // <-- para abrir el menú
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = value.ifEmpty { " " },
                        style = MaterialTheme.typography.bodyMedium,
                        color = BlackColor,
                        modifier = Modifier.weight(1f)
                    )
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = BackgroundColor
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                selectionOption,
                                color = MainColor
                            )
                        },
                        onClick = {
                            onValueChange(selectionOption)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (isError && !errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}






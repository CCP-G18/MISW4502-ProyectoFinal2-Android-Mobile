package com.g18.ccp.ui.auth

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.g18.ccp.R
import com.g18.ccp.core.utils.auth.UiState
import com.g18.ccp.core.utils.error.getErrorMessage
import com.g18.ccp.presentation.auth.LoginViewModel
import com.g18.ccp.ui.theme.BackgroundColor
import com.g18.ccp.ui.theme.BlackColor
import com.g18.ccp.ui.theme.ButtonBackgroundColor
import com.g18.ccp.ui.theme.SecondaryColor

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onBackClick: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val loginState by viewModel.uiState

    LaunchedEffect(loginState) {
        when (loginState) {
            is UiState.Error -> {

            }

            is UiState.Success -> {
                onLoginSuccess()
                viewModel.resetLoginState()
            }

            else -> {
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SecondaryColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .testTag("login_back_button")
                    .align(Alignment.TopStart)
                    .padding(top = 55.dp, start = 16.dp)
                    .size(35.dp)
            ) {
                Icon(
                    modifier = Modifier.size(35.dp),
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = BackgroundColor
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 190.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = getString(context, R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    color = BackgroundColor,
                )

                EmailComponent(context, viewModel)

                PasswordComponent(context, viewModel)

                Spacer(modifier = Modifier.height(56.dp))

                when (loginState) {
                    is UiState.Loading -> CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 36.dp)
                            .height(48.dp)
                    )

                    is UiState.Error -> Text(
                        modifier = Modifier.height(48.dp),
                        text = (loginState as UiState.Error).exception.getErrorMessage(context),
                        color = MaterialTheme.colorScheme.error
                    )

                    else -> {
                        // Do nothing
                    }
                }
                Button(
                    onClick = {
                        viewModel.validateAndLogin()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 36.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonBackgroundColor,
                        contentColor = BlackColor
                    ),
                    enabled = viewModel.dataIsValid() && loginState !is UiState.Loading
                ) {
                    Text(getString(context, R.string.access_text))
                }
                TextButton(
                    onClick = onRegisterClick
                ) {
                    Text(
                        text = "Registrase",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun EmailComponent(context: Context, viewModel: LoginViewModel) {
    var emailError by remember { mutableStateOf<String?>(null) }

    Spacer(modifier = Modifier.height(32.dp))

    CCPTextField(
        value = viewModel.email.value,
        onValueChange = { emailValue ->
            viewModel.onEmailChange(emailValue)
            emailError = getString(context, R.string.invalid_email_text)
                .takeIf { !viewModel.isEmailValid.value }
        },
        label = getString(context, R.string.email_text),
        isError = emailError != null,
        errorMessage = emailError
    )
}

@Composable
private fun PasswordComponent(context: Context, viewModel: LoginViewModel) {
    var showPassword by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Spacer(modifier = Modifier.height(24.dp))

    CCPTextField(
        value = viewModel.password.value,
        onValueChange = { password ->
            viewModel.onPasswordChange(password)
            passwordError =
                getString(context, R.string.min_password_chars_text)
                    .takeIf { !viewModel.isPasswordValid.value }
        },
        label = getString(context, R.string.password_text),
        visualTransformation = if (showPassword) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            val icon =
                if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
            IconButton(onClick = { showPassword = !showPassword }) {
                Icon(
                    imageVector = icon,
                    contentDescription = if (showPassword) "Ocultar" else "Mostrar",
                    tint = BlackColor
                )
            }
        },
        isError = passwordError != null,
        errorMessage = passwordError
    )
}


@Composable
fun CCPTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isError) {
                MaterialTheme.colorScheme.error
            } else {
                BackgroundColor
            },
            modifier = Modifier
                .padding(bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .background(BackgroundColor, RoundedCornerShape(12.dp))
                .border(
                    width = 0.dp,
                    color = if (isError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        Color.Transparent
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .fillMaxWidth()
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                visualTransformation = visualTransformation,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = BlackColor),
                decorationBox = { innerTextField ->
                    Row(
                        modifier.height(36.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.weight(1f)) {
                            innerTextField()
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box {
                            trailingIcon?.let {
                                it()
                            }
                        }
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

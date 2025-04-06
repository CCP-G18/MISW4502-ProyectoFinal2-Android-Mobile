package com.g18.ccp.presentation.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.g18.ccp.R
import com.g18.ccp.presentation.theme.BlackColor
import com.g18.ccp.presentation.theme.ButtonBackgroundColor

@Composable
fun WelcomeScreen(onLoginClick: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.padding(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 194.dp),
            text = getString(context, R.string.entry_title_text),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.background
        )
        Text(
            modifier = Modifier.padding(top = 24.dp),
            text = getString(context, R.string.app_name),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.background
        )

        Spacer(modifier = Modifier.height(152.dp))
        Button(
            modifier = Modifier.size(250.dp, 40.dp),
            onClick = {
                onLoginClick()
            },
            colors = ButtonColors(
                ButtonBackgroundColor,
                BlackColor,
                ButtonBackgroundColor,
                ButtonBackgroundColor
            )
        ) {
            Text(
                text = getString(context, R.string.sign_in_text),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

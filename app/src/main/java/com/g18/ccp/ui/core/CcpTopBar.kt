package com.g18.ccp.ui.core

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.g18.ccp.ui.theme.BackgroundColor
import com.g18.ccp.ui.theme.MainColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CcpTopBar(
    title: String,
    onMenuClick: () -> Unit = {},
    actionIcon: ImageVector? = null,
    onActionClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
            }
        },
        actions = {
            actionIcon?.let {
                IconButton(onClick = onActionClick?: {}) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = "action icon",
                        tint = BackgroundColor
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MainColor,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        )
    )
}

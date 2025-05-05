package com.g18.ccp.ui.core.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItemData(
    val title: String,
    val icon: ImageVector? = null,
    val onClick: () -> Unit = {},
)

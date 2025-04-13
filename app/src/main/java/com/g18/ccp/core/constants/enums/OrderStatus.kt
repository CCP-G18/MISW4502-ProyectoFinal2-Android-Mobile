package com.g18.ccp.core.constants.enums

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.ui.graphics.Color
import com.g18.ccp.R

enum class OrderStatus(
    @StringRes val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
) {
    PREPARING(R.string.status_preparing, Icons.Default.Warehouse, Color(0xFFFFA000)),
    ON_ROUTE(R.string.status_on_route, Icons.Default.LocalShipping, Color(0xFF9C27B0)),
    DELIVERED(R.string.status_delivered, Icons.Default.Check, Color(0xFF4CAF50))
}

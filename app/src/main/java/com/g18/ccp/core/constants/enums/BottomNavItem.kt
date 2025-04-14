package com.g18.ccp.core.constants.enums

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.g18.ccp.R
import com.g18.ccp.core.constants.DELIVERIES_ROUTE
import com.g18.ccp.core.constants.HOME_ROUTE
import com.g18.ccp.core.constants.ORDERS_ROUTE

enum class BottomNavItem(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    HOME(HOME_ROUTE, R.string.app_name, Icons.Outlined.Home),
    ORDERS(ORDERS_ROUTE, R.string.orders_title, Icons.Outlined.ShoppingCart),
    DELIVERIES(DELIVERIES_ROUTE, R.string.deliveries_title, Icons.Outlined.LocalShipping);

    companion object {
        fun fromRoute(route: String?): BottomNavItem? =
            entries.firstOrNull { it.route == route }
    }
}

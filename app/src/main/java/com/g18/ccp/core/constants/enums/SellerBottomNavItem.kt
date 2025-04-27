package com.g18.ccp.core.constants.enums

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.g18.ccp.R
import com.g18.ccp.core.constants.SELLER_HOME_ROUTE

enum class SellerBottomNavItem(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector
) {
    HOME(SELLER_HOME_ROUTE, R.string.customers_text, Icons.Outlined.Home);

    companion object {
        fun fromRoute(route: String?): BottomNavItem? =
            BottomNavItem.entries.firstOrNull { it.route == route }
    }
}

fun SellerBottomNavItem.isHome(): Boolean = this == SellerBottomNavItem.HOME
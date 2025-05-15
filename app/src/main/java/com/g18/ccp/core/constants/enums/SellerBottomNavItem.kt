package com.g18.ccp.core.constants.enums

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.g18.ccp.R
import com.g18.ccp.core.constants.SELLER_CUSTOMERS_ROUTE
import com.g18.ccp.core.constants.SELLER_CUSTOMER_MANAGEMENT_BASE_ROUTE
import com.g18.ccp.core.constants.SELLER_CUSTOMER_MANAGEMENT_ROUTE
import com.g18.ccp.core.constants.SELLER_CUSTOMER_PERSONAL_INFO_BASE_ROUTE
import com.g18.ccp.core.constants.SELLER_CUSTOMER_PERSONAL_INFO_ROUTE
import com.g18.ccp.core.constants.SELLER_CUSTOMER_RECOMMENDATIONS_ROUTE
import com.g18.ccp.core.constants.SELLER_CUSTOMER_VISITS_ROUTE
import com.g18.ccp.core.constants.SELLER_HOME_ROUTE
import com.g18.ccp.core.constants.SELLER_REGISTER_VISIT_ROUTE

enum class SellerBottomNavItem(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector,
    val activeRoutes: List<String>
) {
    HOME(
        route = SELLER_HOME_ROUTE,
        titleRes = R.string.seller_home,
        icon = Icons.Outlined.Home,
        activeRoutes = listOf(
            SELLER_HOME_ROUTE,
            SELLER_CUSTOMERS_ROUTE,
            SELLER_CUSTOMER_MANAGEMENT_ROUTE,
            SELLER_CUSTOMER_PERSONAL_INFO_ROUTE,
            SELLER_CUSTOMER_RECOMMENDATIONS_ROUTE,
            SELLER_CUSTOMER_VISITS_ROUTE,
            SELLER_REGISTER_VISIT_ROUTE,
        )
    ),
    CUSTOMERS(
        route = SELLER_CUSTOMERS_ROUTE,
        titleRes = R.string.customers_text,
        icon = Icons.Filled.People,
        activeRoutes = listOf(
            SELLER_CUSTOMER_MANAGEMENT_BASE_ROUTE,
            SELLER_CUSTOMER_MANAGEMENT_ROUTE,
            SELLER_CUSTOMER_PERSONAL_INFO_ROUTE,
            SELLER_CUSTOMER_RECOMMENDATIONS_ROUTE,
            SELLER_CUSTOMER_VISITS_ROUTE,
        )
    ),
    CUSTOMER(
        route = SELLER_CUSTOMER_PERSONAL_INFO_ROUTE,
        titleRes = R.string.customer_detail_title,
        icon = Icons.Filled.Person,
        activeRoutes = listOf(
            SELLER_CUSTOMER_PERSONAL_INFO_BASE_ROUTE,
            SELLER_CUSTOMER_PERSONAL_INFO_ROUTE,
            SELLER_CUSTOMER_RECOMMENDATIONS_ROUTE,
            SELLER_CUSTOMER_VISITS_ROUTE,
            SELLER_REGISTER_VISIT_ROUTE,
        )
    );
}

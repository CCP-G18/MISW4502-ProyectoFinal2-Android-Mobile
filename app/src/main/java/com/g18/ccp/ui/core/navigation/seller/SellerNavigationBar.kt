package com.g18.ccp.ui.core.navigation.seller

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.g18.ccp.R
import com.g18.ccp.core.constants.CUSTOMER_ID_ARG
import com.g18.ccp.core.constants.SELLER_CUSTOMERS_ROUTE
import com.g18.ccp.core.constants.SELLER_CUSTOMER_MANAGEMENT_ROUTE
import com.g18.ccp.core.constants.SELLER_CUSTOMER_PERSONAL_INFO_ROUTE
import com.g18.ccp.core.constants.SELLER_CUSTOMER_RECOMMENDATIONS_ROUTE
import com.g18.ccp.core.constants.SELLER_CUSTOMER_VISITS_ROUTE
import com.g18.ccp.core.constants.SELLER_HOME_ROUTE
import com.g18.ccp.core.constants.SELLER_PRODUCTS_CATEGORIES_ROUTE
import com.g18.ccp.core.constants.SELLER_REGISTER_VISIT_BASE_ROUTE
import com.g18.ccp.core.constants.SELLER_REGISTER_VISIT_ROUTE
import com.g18.ccp.core.constants.enums.SellerBottomNavItem
import com.g18.ccp.presentation.auth.MainSessionViewModel
import com.g18.ccp.presentation.seller.customermanagement.SellerCustomerManagementViewModel
import com.g18.ccp.presentation.seller.customerslist.SellerCustomersViewModel
import com.g18.ccp.presentation.seller.customervisit.list.SellerCustomerVisitsViewModel
import com.g18.ccp.presentation.seller.home.SellerHomeViewModel
import com.g18.ccp.presentation.seller.order.category.CategoryViewModel
import com.g18.ccp.presentation.seller.personalinfo.SellerCustomerPersonalInfoViewModel
import com.g18.ccp.presentation.seller.recommendation.SellerCustomerRecommendationsViewModel
import com.g18.ccp.ui.core.CcpTopBar
import com.g18.ccp.ui.core.navigation.MenuItemData
import com.g18.ccp.ui.order.seller.category.CategoryScreen
import com.g18.ccp.ui.seller.customer.SellerCustomerListScreen
import com.g18.ccp.ui.seller.customer.customervisit.list.SellerCustomerVisitsScreen
import com.g18.ccp.ui.seller.customer.customervisit.register.SellerRegisterVisitScreen
import com.g18.ccp.ui.seller.customer.management.SellerCustomerManagementScreen
import com.g18.ccp.ui.seller.customer.personalinfo.SellerCustomerPersonalInfoScreen
import com.g18.ccp.ui.seller.customer.recommendation.SellerCustomerRecommendationsScreen
import com.g18.ccp.ui.seller.home.SellerHomeScreen
import com.g18.ccp.ui.theme.SecondaryColor
import com.g18.ccp.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerNavigationBar(
    outNavController: NavHostController,
    navController: NavHostController = rememberNavController(),
    mainSessionViewModel: MainSessionViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val bottomNavItems = remember { SellerBottomNavItem.entries }

    val showBottomBar = currentRoute in listOf(
        SELLER_CUSTOMERS_ROUTE,
        SELLER_CUSTOMER_MANAGEMENT_ROUTE,
        SELLER_CUSTOMER_PERSONAL_INFO_ROUTE,
        SELLER_CUSTOMER_RECOMMENDATIONS_ROUTE,
        SELLER_CUSTOMER_VISITS_ROUTE,
    )

    val title = when (currentRoute) {
        SELLER_HOME_ROUTE -> ""
        SELLER_CUSTOMERS_ROUTE -> stringResource(R.string.customers_text)
        SELLER_CUSTOMER_MANAGEMENT_ROUTE,
        SELLER_CUSTOMER_PERSONAL_INFO_ROUTE -> stringResource(R.string.customer_detail_title)

        SELLER_CUSTOMER_RECOMMENDATIONS_ROUTE -> stringResource(R.string.recommendations_title)

        SELLER_REGISTER_VISIT_ROUTE,
        SELLER_CUSTOMER_VISITS_ROUTE -> stringResource(R.string.visits_title)

        else -> stringResource(R.string.app_name)
    }

    val menuData = listOf(
        MenuItemData(
            icon = Icons.Outlined.Map,
            title = stringResource(R.string.seller_visits_route),
            onClick = { }
        ),
        MenuItemData(
            icon = Icons.Outlined.People,
            title = stringResource(R.string.customers_text),
            onClick = { navController.navigate(SELLER_CUSTOMERS_ROUTE) }
        ),
        MenuItemData(
            icon = Icons.AutoMirrored.Outlined.Logout,
            title = stringResource(R.string.sign_out_text),
            onClick = {
                mainSessionViewModel.performLogout(outNavController)
            }
        ),
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CcpTopBar(
                title = title,
                topBarColor = SecondaryColor,
                menuData = menuData
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = SecondaryColor) {
                    bottomNavItems.forEach { item ->
                        val isSelected = item.route == currentRoute
                        if (item.activeRoutes.contains(currentRoute)) {
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(SellerBottomNavItem.HOME.route)
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        item.icon,
                                        contentDescription = stringResource(item.titleRes),
                                        tint = WhiteColor
                                    )
                                },
                                label = { Text(stringResource(item.titleRes), color = WhiteColor) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = WhiteColor,
                                    unselectedIconColor = WhiteColor.copy(alpha = 0.6f),
                                    selectedTextColor = WhiteColor,
                                    unselectedTextColor = WhiteColor.copy(alpha = 0.6f)
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SELLER_HOME_ROUTE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(SELLER_HOME_ROUTE) {
                val viewModel: SellerHomeViewModel = koinViewModel()
                SellerHomeScreen(
                    onCustomersClick = { navController.navigate(SELLER_CUSTOMERS_ROUTE) },
                    onRoutesClick = { },
                    viewModel = viewModel
                )
            }
            composable(SELLER_CUSTOMERS_ROUTE) {
                val viewModel: SellerCustomersViewModel = koinViewModel()
                SellerCustomerListScreen(
                    viewModel = viewModel,
                    navController = navController,
                )
            }
            composable(
                route = SELLER_CUSTOMER_MANAGEMENT_ROUTE,
                arguments = listOf(navArgument(CUSTOMER_ID_ARG) { type = NavType.StringType })
            ) { backStackEntry ->
                val customerId = backStackEntry.arguments?.getString(CUSTOMER_ID_ARG)
                val viewModel: SellerCustomerManagementViewModel = koinViewModel()

                if (customerId != null) {
                    SellerCustomerManagementScreen(
                        viewModel = viewModel,
                        navController = navController
                    )
                } else {
                    Text(stringResource(R.string.customer_id_not_found_error))
                }
            }
            composable(
                route = SELLER_CUSTOMER_PERSONAL_INFO_ROUTE,
                arguments = listOf(navArgument(CUSTOMER_ID_ARG) { type = NavType.StringType })
            ) { backStackEntry ->
                val viewModel: SellerCustomerPersonalInfoViewModel = koinViewModel()
                SellerCustomerPersonalInfoScreen(
                    viewModel = viewModel
                )
            }
            composable(
                route = SELLER_CUSTOMER_RECOMMENDATIONS_ROUTE,
                arguments = listOf(navArgument(CUSTOMER_ID_ARG) { type = NavType.StringType })
            ) { backStackEntry ->
                val viewModel: SellerCustomerRecommendationsViewModel = koinViewModel()
                SellerCustomerRecommendationsScreen(
                    viewModel = viewModel
                )
            }
            composable(
                route = SELLER_CUSTOMER_VISITS_ROUTE,
                arguments = listOf(navArgument(CUSTOMER_ID_ARG) { type = NavType.StringType })
            ) { backStackEntry ->
                val viewModel: SellerCustomerVisitsViewModel = koinViewModel()
                SellerCustomerVisitsScreen(
                    viewModel = viewModel,
                    onRegisterVisitClick = {
                        val customerId = backStackEntry.arguments?.getString(CUSTOMER_ID_ARG)
                        if (customerId != null)
                            navController.navigate("$SELLER_REGISTER_VISIT_BASE_ROUTE/$customerId")
                    }
                )
            }
            composable(
                route = SELLER_REGISTER_VISIT_ROUTE,
                arguments = listOf(navArgument(CUSTOMER_ID_ARG) { type = NavType.StringType })
            ) {
                SellerRegisterVisitScreen(
                    onVisitCompletedAndNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = SELLER_PRODUCTS_CATEGORIES_ROUTE,
                arguments = listOf(navArgument(CUSTOMER_ID_ARG) { type = NavType.StringType })
            ) {
                val viewModel: CategoryViewModel = koinViewModel()
                CategoryScreen(
                    viewModel,
                    navController
                )
            }
        }
    }
}

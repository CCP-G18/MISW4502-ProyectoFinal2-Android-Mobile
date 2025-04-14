package com.g18.ccp.ui.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.g18.ccp.core.constants.ORDER_DETAIL_ROUTE
import com.g18.ccp.core.constants.enums.BottomNavItem
import com.g18.ccp.data.remote.model.order.Order
import com.g18.ccp.presentation.order.OrdersViewModel
import com.g18.ccp.ui.core.CcpTopBar
import com.g18.ccp.ui.home.HomeScreen
import com.g18.ccp.ui.order.status.OrdersScreen
import com.g18.ccp.ui.order.status.SELECTED_ORDER_KEY
import com.g18.ccp.ui.order.status.detail.OrderDetailScreen
import com.g18.ccp.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainWithNavigationBar() {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route
    val currentItem = BottomNavItem.fromRoute(currentRoute) ?: BottomNavItem.ORDERS

    Scaffold(
        containerColor = WhiteColor,
        topBar = {
            CcpTopBar(
                title = stringResource(currentItem.titleRes),
                onMenuClick = { /* handle menu click */ }
            )
        },
        bottomBar = {
            NavigationBarComponent(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.ORDERS.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.HOME.route) { HomeScreen() }

            composable(ORDER_DETAIL_ROUTE) {
                val order = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Order>(SELECTED_ORDER_KEY)

                order?.let {
                    OrderDetailScreen(order = it)
                }
            }
            composable(BottomNavItem.ORDERS.route) {
                val viewModel: OrdersViewModel = koinViewModel()
                OrdersScreen(viewModel, navController)
            }
            composable(BottomNavItem.DELIVERIES.route) {
//                DeliveryScreen()
            }
        }
    }
}

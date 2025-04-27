package com.g18.ccp.ui.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.g18.ccp.R
import com.g18.ccp.core.constants.CART_ROUTE
import com.g18.ccp.core.constants.ORDER_DETAIL_ROUTE
import com.g18.ccp.core.constants.SPLASH_CONGRATS_ROUTE
import com.g18.ccp.core.constants.enums.BottomNavItem
import com.g18.ccp.core.constants.enums.isHome
import com.g18.ccp.data.remote.model.order.Order
import com.g18.ccp.presentation.order.OrdersViewModel
import com.g18.ccp.presentation.order.create.ListProductViewModel
import com.g18.ccp.ui.core.CcpTopBar
import com.g18.ccp.ui.order.cart.CartScreen
import com.g18.ccp.ui.order.congrats.SplashCongratsScreen
import com.g18.ccp.ui.order.status.OrdersScreen
import com.g18.ccp.ui.order.status.SELECTED_ORDER_KEY
import com.g18.ccp.ui.order.status.detail.OrderDetailScreen
import com.g18.ccp.ui.product.ProductListScreen
import com.g18.ccp.ui.theme.WhiteColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun CustomerNavigationBar() {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route

    val isCartScreen = currentRoute == CART_ROUTE
    val currentItem = BottomNavItem.fromRoute(currentRoute) ?: BottomNavItem.ORDERS
    val listProductViewModel = koinViewModel<ListProductViewModel>()

    val title = when {
        isCartScreen -> stringResource(R.string.cart_title)
        else -> stringResource(currentItem.titleRes)
    }

    Scaffold(
        containerColor = WhiteColor,
        topBar = {
            CcpTopBar(
                title = title,
                onMenuClick = { /* handle menu click */ },
                actionIcon = if (currentItem.isHome()) Icons.Outlined.ShoppingCart else null,
                onActionClick = {
                    if (currentItem.isHome()) {
                        navController.navigate(CART_ROUTE)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBarComponent(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(SPLASH_CONGRATS_ROUTE) {
                SplashCongratsScreen(navController)
            }
            composable(CART_ROUTE) {
                CartScreen(viewModel = listProductViewModel, navController = navController)
            }
            composable(BottomNavItem.HOME.route) {
                ProductListScreen(viewModel = listProductViewModel, onCartClick = {})
            }

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

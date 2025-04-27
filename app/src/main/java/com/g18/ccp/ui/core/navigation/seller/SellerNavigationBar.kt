package com.g18.ccp.ui.core.navigation.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.g18.ccp.R
import com.g18.ccp.core.constants.SELLER_CUSTOMERS_ROUTE
import com.g18.ccp.core.constants.SELLER_HOME_ROUTE
import com.g18.ccp.presentation.seller.customerslist.SellerCustomersViewModel
import com.g18.ccp.presentation.seller.home.SellerHomeViewModel
import com.g18.ccp.ui.core.CcpTopBar
import com.g18.ccp.ui.seller.customer.SellerCustomerListScreen
import com.g18.ccp.ui.seller.home.SellerHomeScreen
import com.g18.ccp.ui.theme.BackgroundColor
import com.g18.ccp.ui.theme.MainColor
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerNavigationBar(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute == SELLER_CUSTOMERS_ROUTE

    val title = stringResource(R.string.customers_text)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CcpTopBar(
                title = title,
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MainColor) {
                    NavigationBarItem(
                        selected = currentRoute == SELLER_CUSTOMERS_ROUTE,
                        onClick = { navController.navigate(SELLER_CUSTOMERS_ROUTE) },
                        icon = {
                            Icon(
                                Icons.Outlined.Home, contentDescription = null,
                                modifier = Modifier.background(
                                    Color.Transparent
                                ),
                            )
                        },
                        label = {
                            Text(
                                stringResource(R.string.seller_home),
                                color = BackgroundColor
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BackgroundColor,
                            unselectedIconColor = BackgroundColor.copy(alpha = 0.6f),
                            selectedTextColor = BackgroundColor,
                            unselectedTextColor = BackgroundColor.copy(alpha = 0.6f),
                            indicatorColor = BackgroundColor.copy(alpha = 0.1f)
                        )
                    )
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
                    viewModel = viewModel
                )
            }
        }
    }
}

package com.g18.ccp.ui.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.g18.ccp.core.constants.HOME_ROUTE
import com.g18.ccp.core.constants.LOGIN_ROUTE
import com.g18.ccp.core.constants.ORDERS_ROUTE
import com.g18.ccp.core.constants.REGISTER_ROUTE
import com.g18.ccp.core.constants.SELLER_HOME_ROUTE
import com.g18.ccp.core.constants.WELCOME_ROUTE
import com.g18.ccp.presentation.auth.LoginViewModel
import com.g18.ccp.presentation.auth.RegisterClientViewModel
import com.g18.ccp.ui.auth.LoginScreen
import com.g18.ccp.ui.auth.RegisterClientScreen
import com.g18.ccp.ui.auth.WelcomeScreen
import com.g18.ccp.ui.core.navigation.seller.SellerNavigationBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(navController = navController, startDestination = WELCOME_ROUTE) {
        composable(WELCOME_ROUTE) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(LOGIN_ROUTE) },
            )
        }
        composable(LOGIN_ROUTE) {
            val viewModel: LoginViewModel = koinViewModel()
            LoginScreen(
                viewModel = viewModel,
                onBackClick = { navController.navigate(WELCOME_ROUTE) },
                onLoginSuccess = { role ->
                    if (role == "seller") {
                        navController.navigate(SELLER_HOME_ROUTE)
                    } else {
                        navController.navigate(ORDERS_ROUTE)
                    }
                },
                onRegisterClick = { navController.navigate(REGISTER_ROUTE) },
            )
        }
        composable(REGISTER_ROUTE) {
            val viewModel: RegisterClientViewModel = koinViewModel()
            RegisterClientScreen(
                viewModel = viewModel,
                onBackClick = { navController.navigate(WELCOME_ROUTE) },
                onRegisterSuccess = { navController.navigate(HOME_ROUTE) }
            )
        }
        composable(ORDERS_ROUTE) {
            CustomerNavigationBar()
        }
        composable(SELLER_HOME_ROUTE) {
            SellerNavigationBar()
        }
    }
}

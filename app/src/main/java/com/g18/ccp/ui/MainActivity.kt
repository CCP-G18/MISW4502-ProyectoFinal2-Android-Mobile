package com.g18.ccp.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.g18.ccp.di.authModule
import com.g18.ccp.di.coreModule
import com.g18.ccp.di.networkModule
import com.g18.ccp.presentation.auth.LoginViewModel
import com.g18.ccp.presentation.auth.RegisterClientViewModel
import com.g18.ccp.ui.auth.LoginScreen
import com.g18.ccp.ui.auth.RegisterClientScreen
import com.g18.ccp.ui.auth.WelcomeScreen
import com.g18.ccp.ui.home.HomeScreen
import com.g18.ccp.ui.theme.CCPTheme
import com.g18.ccp.ui.theme.SecondaryColor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@MainActivity)
            modules(networkModule)
            modules(authModule)
            modules(coreModule)
        }
        enableEdgeToEdge()
        setContent {
            CCPTheme(
                darkTheme = true,
                dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SecondaryColor
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(
                onLoginClick = { navController.navigate("login") },
            )
        }
        composable("login") {
            val viewModel: LoginViewModel = koinViewModel()
            LoginScreen(
                viewModel = viewModel,
                onBackClick = { navController.navigate("welcome") },
                onLoginSuccess = { navController.navigate("home") },
                onRegisterClick = { navController.navigate("register") },
            )
        }
        composable("register") {
            val viewModel: RegisterClientViewModel = koinViewModel()
            RegisterClientScreen(
                viewModel = viewModel,
                onBackClick = { navController.navigate("welcome") },
                onRegisterSuccess = { navController.navigate("home") }
            )
        }
        composable("home") {
            HomeScreen()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CCPTheme {
//        ContentEntryScreen("Android")
    }
}

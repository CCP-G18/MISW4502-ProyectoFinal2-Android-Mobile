package com.g18.ccp.presentation.ui

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
import com.g18.ccp.presentation.theme.CCPTheme
import com.g18.ccp.presentation.theme.SecondaryColor
import com.g18.ccp.presentation.ui.auth.LoginScreen
import com.g18.ccp.presentation.ui.auth.RegisterScreen
import com.g18.ccp.presentation.ui.auth.WelcomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            LoginScreen()
        }
        composable("register") {
            RegisterScreen()
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

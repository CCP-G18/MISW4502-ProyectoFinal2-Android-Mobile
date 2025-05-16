package com.g18.ccp.ui.order.congrats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.g18.ccp.core.constants.ORDERS_ROUTE
import com.g18.ccp.core.constants.SPLASH_CONGRATS_ROUTE
import com.g18.ccp.ui.theme.MainAlertColor
import kotlinx.coroutines.delay

private const val SCREEN_DELAY = 3000L
@Composable
fun SplashCongratsScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(SCREEN_DELAY)
        navController.navigate(ORDERS_ROUTE) {
            popUpTo(SPLASH_CONGRATS_ROUTE) { inclusive = true }
            launchSingleTop = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainAlertColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = "Check",
            tint = Color.White,
            modifier = Modifier.size(64.dp)
        )
    }
}

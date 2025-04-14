package com.g18.ccp.ui.core.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.g18.ccp.core.constants.enums.BottomNavItem
import com.g18.ccp.ui.theme.MainColor

@Composable
fun NavigationBarComponent(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar (
        containerColor = MaterialTheme.colorScheme.background
    ) {
        BottomNavItem.entries.forEach { item ->
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MainColor,
                    unselectedIconColor = MainColor.copy(alpha = 0.6f),
                    selectedTextColor = MainColor,
                    unselectedTextColor = MainColor,
                    indicatorColor = MainColor.copy(alpha = 0.1f)
                ),
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = { Text(stringResource(item.titleRes)) },
                icon = { Icon(item.icon, contentDescription = stringResource(item.titleRes)) }
            )
        }
    }
}

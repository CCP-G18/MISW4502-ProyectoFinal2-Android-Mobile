package com.g18.ccp.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.g18.ccp.core.constants.LOGIN_ROUTE
import com.g18.ccp.repository.auth.LoginRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainSessionViewModel(
    private val loginRepository: LoginRepository
) : ViewModel() {

    fun performLogout(
        navController: NavController,
        dispatcher: CoroutineDispatcher = Dispatchers.Main
    ) {
        viewModelScope.launch(dispatcher) {
            loginRepository.logout()
            navController.navigate(LOGIN_ROUTE) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }
}

package com.g18.ccp.repository.auth

import android.util.Log
import com.g18.ccp.core.session.UserSessionManager
import com.g18.ccp.core.utils.auth.AuthenticationManager
import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.data.local.Datasource
import com.g18.ccp.data.remote.model.auth.LoginRequest
import com.g18.ccp.data.remote.model.auth.UserInfo
import com.g18.ccp.data.remote.service.auth.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginRepositoryImpl(
    private val authService: AuthService,
    private val authenticationManager: AuthenticationManager,
    private val datasource: Datasource
) : LoginRepository {
    override suspend fun login(email: String, password: String) =
        try {
            val response = authService.login(LoginRequest(email, password))
            authenticationManager.saveToken(response.data.accessToken)
            saveUserInfo(response.data.user)
            Output.Success(Unit)
        } catch (e: Exception) {
            Output.Failure(e, e.message)
        }

    override suspend fun logout() {
        withContext(Dispatchers.IO) {
            try {
                authenticationManager.clearToken()
                UserSessionManager.clearSession(datasource)
                Log.i("LoginRepositoryImpl", "Session data cleared.")
            } catch (e: Exception) {
                Log.e("LoginRepositoryImpl", "Error during logout data clearing", e)
            }
        }
    }

    override suspend fun getUserRole(): String =
        UserSessionManager.getUserInfo(datasource)?.role.orEmpty()

    private suspend fun saveUserInfo(userInfo: UserInfo) {
        UserSessionManager.saveUserInfo(datasource, userInfo)
    }
}

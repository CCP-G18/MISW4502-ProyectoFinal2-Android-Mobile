package com.g18.ccp.repository.user

import com.g18.ccp.core.session.UserSessionManager
import com.g18.ccp.data.local.Datasource
import com.g18.ccp.data.remote.model.auth.UserInfo

class UserRepositoryImpl(private val datasource: Datasource) : UserRepository {
    override suspend fun getUserName(): String =
        UserSessionManager.getUserInfo(datasource)?.username.orEmpty()

    override suspend fun getUserInfoById(userId: String): UserInfo? =
        UserSessionManager.getUserInfo(datasource)?.let {
            if (it.id == userId) it else null
        }
}

package com.g18.ccp.repository.user

import com.g18.ccp.core.session.UserSessionManager
import com.g18.ccp.data.local.Datasource

class UserRepositoryImpl(private val datasource: Datasource) : UserRepository {
    override suspend fun getUserName(): String =
        UserSessionManager.getUserInfo(datasource)?.username.orEmpty()
}

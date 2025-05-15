package com.g18.ccp.repository.user

import com.g18.ccp.data.remote.model.auth.UserInfo

interface UserRepository {
    suspend fun getUserName(): String
    suspend fun getUserInfoById(userId: String): UserInfo?
}

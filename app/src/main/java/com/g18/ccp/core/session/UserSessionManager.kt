package com.g18.ccp.core.session

import com.g18.ccp.core.constants.USER_INFO_KEY
import com.g18.ccp.data.local.Datasource
import com.g18.ccp.data.remote.model.auth.UserInfo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object UserSessionManager {

    suspend fun saveUserInfo(datasource: Datasource, userInfo: UserInfo) {
        val json = Json.encodeToString(userInfo)
        datasource.putString(USER_INFO_KEY, json)
    }

    suspend fun getUserInfo(datasource: Datasource): UserInfo? {
        val json = datasource.getString(USER_INFO_KEY) ?: return null
        return try {
            Json.decodeFromString<UserInfo>(json)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun clearSession(datasource: Datasource) {
        datasource.remove(USER_INFO_KEY)
    }
}

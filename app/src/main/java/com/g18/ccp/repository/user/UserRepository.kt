package com.g18.ccp.repository.user

interface UserRepository {
    suspend fun getUserName(): String
}

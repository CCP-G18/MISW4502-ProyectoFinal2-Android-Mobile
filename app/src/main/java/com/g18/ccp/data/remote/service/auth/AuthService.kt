package com.g18.ccp.data.remote.service.auth

import com.g18.ccp.data.remote.model.auth.LoginRequest
import com.g18.ccp.data.remote.model.auth.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}

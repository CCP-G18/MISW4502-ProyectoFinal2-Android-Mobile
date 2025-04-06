package com.g18.ccp.core.utils.auth

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authenticationManager: AuthenticationManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = authenticationManager.getToken()
        val requestBuilder = chain.request().newBuilder()
        if (!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(requestBuilder.build())
    }
}

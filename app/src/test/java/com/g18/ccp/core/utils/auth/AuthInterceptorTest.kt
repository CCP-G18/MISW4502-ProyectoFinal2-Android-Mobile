package com.g18.ccp.core.utils.auth

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthInterceptorTest {

    @Test
    fun `given valid token when intercept then adds Authorization header`() {
        // Given
        val token = "abc123"
        val authenticationManager = mockk<AuthenticationManager>()
        every { authenticationManager.getToken() } returns token

        val originalRequest = Request.Builder()
            .url("https://example.com")
            .build()

        val chain = mockk<Interceptor.Chain>()
        val capturedRequest = slot<Request>()
        val mockResponse = mockk<Response>()

        every { chain.request() } returns originalRequest
        every { chain.proceed(capture(capturedRequest)) } returns mockResponse

        val interceptor = AuthInterceptor(authenticationManager)

        // When
        val result = interceptor.intercept(chain)

        // Then
        assertEquals(mockResponse, result)
        val requestWithHeader = capturedRequest.captured
        assertEquals("Bearer $token", requestWithHeader.header("Authorization"))
    }

    @Test
    fun `given null token when intercept then does not add Authorization header`() {
        // Given
        val authenticationManager = mockk<AuthenticationManager>()
        every { authenticationManager.getToken() } returns null

        val originalRequest = Request.Builder()
            .url("https://example.com")
            .build()

        val chain = mockk<Interceptor.Chain>()
        val capturedRequest = slot<Request>()
        val mockResponse = mockk<Response>()

        every { chain.request() } returns originalRequest
        every { chain.proceed(capture(capturedRequest)) } returns mockResponse

        val interceptor = AuthInterceptor(authenticationManager)

        // When
        val result = interceptor.intercept(chain)

        // Then
        assertEquals(mockResponse, result)
        val requestWithoutHeader = capturedRequest.captured
        assertNull(requestWithoutHeader.header("Authorization"))
    }
}


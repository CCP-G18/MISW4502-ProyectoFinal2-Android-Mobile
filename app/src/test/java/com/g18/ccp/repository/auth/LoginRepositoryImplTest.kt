package com.g18.ccp.repository.auth

import com.g18.ccp.core.constants.USER_INFO_KEY
import com.g18.ccp.core.utils.auth.AuthenticationManager
import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.data.local.Datasource
import com.g18.ccp.data.remote.model.auth.LoginData
import com.g18.ccp.data.remote.model.auth.LoginRequest
import com.g18.ccp.data.remote.model.auth.LoginResponse
import com.g18.ccp.data.remote.model.auth.UserInfo
import com.g18.ccp.data.remote.service.auth.AuthService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginRepositoryImplTest {

    private lateinit var authService: AuthService
    private lateinit var authManager: AuthenticationManager
    private lateinit var datasource: Datasource
    private lateinit var repository: LoginRepositoryImpl

    private val fakeUser = UserInfo(
        email = "test@email.com",
        id = "123",
        role = "admin",
        username = "testUser"
    )

    private val fakeToken = "fake_token"

    @Before
    fun setUp() {
        authService = mockk()
        authManager = mockk(relaxed = true)
        datasource = mockk(relaxed = true)
        repository = LoginRepositoryImpl(authService, authManager, datasource)
    }

    @Test
    fun `given valid credentials when login then returns success and saves token and user info`() =
        runTest {
            // Given
            val request = LoginRequest("testUser", "123456")
            val loginData = LoginData(accessToken = fakeToken, user = fakeUser)
            val response = LoginResponse(
                code = 200,
                data = loginData,
                message = "OK",
                status = "success"
            )
            coEvery { authService.login(request) } returns response

            // When
            val result = repository.login(request.username, request.password)

            // Then
            assertTrue(result is Output.Success)
            coVerify { authManager.saveToken(fakeToken) }
            coVerify { datasource.putString(USER_INFO_KEY, Json.encodeToString(fakeUser)) }
        }

    @Test
    fun `given service throws exception when login then returns failure`() = runTest {
        // Given
        val request = LoginRequest("errorUser", "wrongpass")
        val exception = Exception("Login failed")
        coEvery { authService.login(request) } throws exception

        // When
        val result = repository.login(request.username, request.password)

        // Then
        assertTrue(result is Output.Failure<*>)
        val failure = result as Output.Failure<*>
        assertEquals(exception, failure.exception)
        assertEquals("Login failed", failure.message)
    }
}


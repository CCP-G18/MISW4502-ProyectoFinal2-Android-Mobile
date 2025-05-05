package com.g18.ccp.repository.auth

import android.util.Log
import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.core.constants.USER_INFO_KEY
import com.g18.ccp.core.session.UserSessionManager
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
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class LoginRepositoryImplTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var authService: AuthService

    @MockK
    private lateinit var authManager: AuthenticationManager

    @MockK
    private lateinit var datasource: Datasource

    private lateinit var repository: LoginRepositoryImpl

    private val fakeUser = UserInfo(
        email = "test@email.com",
        id = "123",
        role = "admin",
        username = "testUser"
    )
    private val fakeToken = "fake_token"
    private val fakeUserJson = Json.encodeToString(fakeUser)

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        repository = LoginRepositoryImpl(authService, authManager, datasource)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
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
            every { authManager.saveToken(fakeToken) } just runs
            coEvery { UserSessionManager.saveUserInfo(datasource, fakeUser) } just runs
            coEvery { datasource.putString(USER_INFO_KEY, fakeUserJson) } just runs


            val result = repository.login(request.username, request.password)
            advanceUntilIdle()


            assertTrue(result is Output.Success)
            coVerify(exactly = 1) { authService.login(request) }
            verify(exactly = 1) { authManager.saveToken(fakeToken) }
            coVerify(exactly = 1) { UserSessionManager.saveUserInfo(datasource, fakeUser) }
            coVerify(exactly = 1) { datasource.putString(USER_INFO_KEY, fakeUserJson) }
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

    @Test
    fun `when logout is called - then clears token and session`() = runTest {
        mockkStatic(UserSessionManager::class)
        every { authManager.clearToken() } just runs
        coEvery { UserSessionManager.clearSession(datasource) } just runs
        coEvery { datasource.remove(USER_INFO_KEY) } just runs


        repository.logout()
        advanceUntilIdle()


        verify(exactly = 1) { authManager.clearToken() }
        coVerify(exactly = 1) { UserSessionManager.clearSession(datasource) }
        coVerify(exactly = 1) { datasource.remove(USER_INFO_KEY) }
        unmockkStatic(UserSessionManager::class)
    }

    @Test
    fun `given clearToken throws - when logout - then still calls clearSession and logs error`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val exception = RuntimeException("Failed to clear token")
            every { authManager.clearToken() } throws exception
            coEvery { UserSessionManager.clearSession(datasource) } just runs
            coEvery { datasource.remove(USER_INFO_KEY) } just runs


            repository.logout()
            advanceUntilIdle()


            verify(exactly = 1) { authManager.clearToken() }
            coVerify(exactly = 0) { UserSessionManager.clearSession(datasource) } // Should not be called if clearToken throws before it in try block
            coVerify(exactly = 0) { datasource.remove(USER_INFO_KEY) }
            verify {
                Log.e(
                    eq("LoginRepositoryImpl"),
                    eq("Error during logout data clearing"),
                    eq(exception)
                )
            }
        }

    @Test
    fun `given clearSession throws - when logout - then calls clearToken and logs error`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val exception = IOException("Failed to clear session")
            every { authManager.clearToken() } just runs
            coEvery { UserSessionManager.clearSession(datasource) } throws exception


            repository.logout()
            advanceUntilIdle()


            verify(exactly = 1) { authManager.clearToken() }
            coVerify(exactly = 1) { UserSessionManager.clearSession(datasource) }
            verify {
                Log.e(
                    eq("LoginRepositoryImpl"),
                    eq("Error during logout data clearing"),
                    eq(exception)
                )
            }
        }
}

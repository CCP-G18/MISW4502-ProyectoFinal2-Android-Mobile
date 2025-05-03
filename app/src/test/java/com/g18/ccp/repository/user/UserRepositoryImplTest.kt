package com.g18.ccp.repository.user

import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.core.session.UserSessionManager
import com.g18.ccp.data.local.Datasource
import com.g18.ccp.data.remote.model.auth.UserInfo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class UserRepositoryImplTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var datasource: Datasource

    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setUp() {
        mockkObject(UserSessionManager)
        repository = UserRepositoryImpl(datasource)
    }

    @After
    fun tearDown() {
        unmockkObject(UserSessionManager)
    }

    @Test
    fun `given UserSessionManager Returns UserInfo - when getUserName is Called - then Returns Username`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val expectedUsername = "testUser"
            val fakeUserInfo =
                UserInfo(email = "a@b.com", id = "1", role = "seller", username = expectedUsername)
            coEvery { UserSessionManager.getUserInfo(datasource) } returns fakeUserInfo

            val result = repository.getUserName()

            assertEquals(expectedUsername, result)
            coVerify(exactly = 1) { UserSessionManager.getUserInfo(datasource) }
        }

    @Test
    fun `given UserSessionManager Returns Null - when getUserName is Called - then Returns Empty String`() =
        runTest(mainDispatcherRule.testDispatcher) {
            coEvery { UserSessionManager.getUserInfo(datasource) } returns null

            val result = repository.getUserName()

            assertEquals("", result)
            coVerify(exactly = 1) { UserSessionManager.getUserInfo(datasource) }
        }

    @Test
    fun `given UserSessionManager Throws Exception - when getUserName is Called - then Propagates Exception`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val expectedException = IOException("Failed to get session")
            coEvery { UserSessionManager.getUserInfo(datasource) } throws expectedException

            var caughtException: Throwable? = null
            try {
                repository.getUserName()
                fail("Expected IOException was not thrown")
            } catch (e: IOException) {
                caughtException = e
            } catch (e: Throwable) {
                fail("Unexpected exception type thrown: ${e::class.java.simpleName}")
            }

            assertNotNull(caughtException)
            assertTrue(caughtException is IOException)
            assertEquals(expectedException.message, caughtException!!.message)
            coVerify(exactly = 1) { UserSessionManager.getUserInfo(datasource) }
        }
}

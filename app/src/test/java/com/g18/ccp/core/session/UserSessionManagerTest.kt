package com.g18.ccp.core.session

import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.core.constants.USER_INFO_KEY
import com.g18.ccp.data.local.Datasource
import com.g18.ccp.data.remote.model.auth.UserInfo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UserSessionManagerTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var datasource: Datasource

    private lateinit var testUserInfo: UserInfo
    private lateinit var testUserInfoJson: String

    @Before
    fun setUp() {
        testUserInfo = UserInfo(
            email = "test@example.com",
            id = "user123",
            role = "seller",
            username = "tester"
        )
        testUserInfoJson = Json.encodeToString(testUserInfo)
    }

    @Test
    fun `given UserInfo - when saveUserInfo is Called - then Datasource PutString Is Called With Correct Key And Json`() =
        runTest {
            coEvery { datasource.putString(USER_INFO_KEY, testUserInfoJson) } just runs

            UserSessionManager.saveUserInfo(datasource, testUserInfo)

            coVerify(exactly = 1) { datasource.putString(USER_INFO_KEY, testUserInfoJson) }
        }

    @Test
    fun `given Datasource Has Valid UserInfo Json - when getUserInfo is Called - then Returns Deserialized UserInfo`() =
        runTest {
            coEvery { datasource.getString(USER_INFO_KEY) } returns testUserInfoJson

            val result = UserSessionManager.getUserInfo(datasource)

            assertNotNull(result)
            assertEquals(testUserInfo, result)
            coVerify(exactly = 1) { datasource.getString(USER_INFO_KEY) }
        }

    @Test
    fun `given Datasource Returns Null - when getUserInfo is Called - then Returns Null`() =
        runTest {
            coEvery { datasource.getString(USER_INFO_KEY) } returns null

            val result = UserSessionManager.getUserInfo(datasource)

            assertNull(result)
            coVerify(exactly = 1) { datasource.getString(USER_INFO_KEY) }
        }

    @Test
    fun `given Datasource Returns Invalid Json - when getUserInfo is Called - then Returns Null`() =
        runTest {
            val invalidJson = "this is not json"
            coEvery { datasource.getString(USER_INFO_KEY) } returns invalidJson

            val result = UserSessionManager.getUserInfo(datasource)

            assertNull(result)
            coVerify(exactly = 1) { datasource.getString(USER_INFO_KEY) }
        }


    @Test
    fun `when clearSession is Called - then Datasource Remove Is Called With Correct Key`() =
        runTest {
            coEvery { datasource.remove(USER_INFO_KEY) } just runs

            UserSessionManager.clearSession(datasource)

            coVerify(exactly = 1) { datasource.remove(USER_INFO_KEY) }
        }
}

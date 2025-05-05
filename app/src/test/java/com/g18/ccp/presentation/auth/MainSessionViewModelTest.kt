package com.g18.ccp.presentation.auth

import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavOptionsBuilder
import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.core.constants.LOGIN_ROUTE
import com.g18.ccp.repository.auth.LoginRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class MainSessionViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var loginRepository: LoginRepository

    @MockK
    private lateinit var navController: NavController

    @MockK
    private lateinit var navGraph: NavGraph

    private lateinit var viewModel: MainSessionViewModel

    private val testGraphId = 123 // Example graph ID

    @Before
    fun setUp() {
        every { navController.graph } returns navGraph
        every { navGraph.id } returns testGraphId
        viewModel = MainSessionViewModel(loginRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `given Logout Succeeds - when performLogout - then Calls Repository Logout And Navigates With PopUp`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val navOptionsBuilderSlot = slot<NavOptionsBuilder.() -> Unit>()
            coEvery { loginRepository.logout() } just runs
            every {
                navController.navigate(
                    eq(LOGIN_ROUTE),
                    capture(navOptionsBuilderSlot)
                )
            } just runs

            viewModel.performLogout(navController)
            advanceUntilIdle()

            coVerify(exactly = 1) { loginRepository.logout() }

            // Verify NavOptions configuration (basic check)
            val mockBuilder =
                mockk<NavOptionsBuilder>(relaxed = true)
            navOptionsBuilderSlot.captured.invoke(mockBuilder)
            verify {
                mockBuilder.popUpTo(
                    eq(testGraphId),
                    any()
                )
            } // Verify popUpTo graph ID was called
            verify { mockBuilder.launchSingleTop = true } // Verify launchSingleTop was set
        }

    @Test(expected = IOException::class)
    fun `given Logout Fails - when performLogout - then Calls Repository Logout And Does Not Navigate`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val exception = IOException("Failed to clear session")
            coEvery { loginRepository.logout() } throws exception

            viewModel.performLogout(navController)
            advanceUntilIdle()

            coVerify(exactly = 1) { loginRepository.logout() }
        }

}

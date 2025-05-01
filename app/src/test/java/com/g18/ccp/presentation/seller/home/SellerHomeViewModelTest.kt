package com.g18.ccp.presentation.seller.home

import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.repository.user.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SellerHomeViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var userRepository: UserRepository

    private lateinit var viewModel: SellerHomeViewModel

    @Before
    fun setUp() {
        userRepository = mockk()
    }

    @Test
    fun `given UserRepository Returns Name - when ViewModel Initializes - then UserName StateFlow Updates`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val expectedName = "Test Seller"
            coEvery { userRepository.getUserName() } returns expectedName

            viewModel = SellerHomeViewModel(userRepository)
            advanceUntilIdle()

            assertEquals(expectedName, viewModel.userName.value)
            coVerify(exactly = 1) { userRepository.getUserName() }
        }

    @Test
    fun `given UserRepository Returns Null - when ViewModel Initializes - then UserName StateFlow Is Null`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val expectedName: String = "testName"
            coEvery { userRepository.getUserName() } returns expectedName

            viewModel = SellerHomeViewModel(userRepository)
            advanceUntilIdle()

            assertEquals(expectedName, viewModel.userName.value)
            coVerify(exactly = 1) { userRepository.getUserName() }
        }
}

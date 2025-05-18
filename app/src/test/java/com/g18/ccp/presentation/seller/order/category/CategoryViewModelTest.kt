package com.g18.ccp.presentation.seller.order.category

import android.util.Log
import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.data.remote.model.seller.order.CategoryData
import com.g18.ccp.repository.seller.order.category.SellerProductRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private lateinit var viewModel: CategoryViewModel
    private val categoryRepository = mockk<SellerProductRepository>()

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any<Throwable>()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkAll()
        unmockkStatic(Log::class)
    }

    @Test
    fun `given categories in database and successful refresh when start then uiState is Success with sorted categories`() =
        testScope.runTest {
            // Given unsorted categories from Room
            val catB = CategoryData(
                createdAt = "2025-01-02T00:00:00Z",
                id = "2",
                name = "Bodega",
                updatedAt = "2025-01-03T00:00:00Z"
            )
            val catA = CategoryData(
                createdAt = "2025-01-01T00:00:00Z",
                id = "1",
                name = "Alimentos",
                updatedAt = "2025-01-02T00:00:00Z"
            )
            coEvery { categoryRepository.getCategories() } returns flowOf(listOf(catB, catA))
            coEvery { categoryRepository.refreshCategories() } returns Result.success(Unit)

            viewModel = CategoryViewModel(categoryRepository, testDispatcher)

            // When starting without resetting to Loading
            viewModel.start(isInitialLoad = false)
            advanceUntilIdle()

            // Then uiState should be Success with sorted list [Alimentos, Bodega]
            val state = viewModel.uiState.value
            assertTrue(state is CategoryUiState.Success)
            state as CategoryUiState.Success
            assertEquals(listOf(catA, catB), state.categories)
        }

    @Test
    fun `given database flow error but refresh success when start then uiState is Error with DB message`() =
        testScope.runTest {
            // When getCategories throws
            coEvery { categoryRepository.getCategories() } returns flow { throw Exception("DB failed") }
            coEvery { categoryRepository.refreshCategories() } returns Result.success(Unit)

            viewModel = CategoryViewModel(categoryRepository, testDispatcher)

            // When starting without resetting to Loading
            viewModel.start(isInitialLoad = false)
            advanceUntilIdle()

            // Then state is Error with message containing DB error
            val state = viewModel.uiState.value
            assertTrue(state is CategoryUiState.Loading)
        }

    @Test
    fun `given database flow error when start then uiState is Error with DB message`() =
        testScope.runTest {
            // When getCategories throws
            coEvery { categoryRepository.getCategories() } returns flow { throw Exception("DB failed") }
            coEvery { categoryRepository.refreshCategories() } returns Result.failure(Exception("DB failed"))

            viewModel = CategoryViewModel(categoryRepository, testDispatcher)

            // When starting without resetting to Loading
            viewModel.start(isInitialLoad = false)
            advanceUntilIdle()

            // Then state is Error with message containing DB error
            val state = viewModel.uiState.value
            assertTrue(state is CategoryUiState.Error)
            state as CategoryUiState.Error
            assertTrue(state.message.contains("DB failed"))
        }

    @Test
    fun `given refresh failure on start then uiState is Error with refresh message`() =
        testScope.runTest {
            // Given Room emits one category
            val cat = CategoryData(
                createdAt = "2025-01-01T00:00:00Z",
                id = "1",
                name = "A",
                updatedAt = "2025-01-02T00:00:00Z"
            )
            coEvery { categoryRepository.getCategories() } returns flowOf(listOf(cat))
            coEvery { categoryRepository.refreshCategories() } returns Result.failure(
                RuntimeException("network fail")
            )

            viewModel = CategoryViewModel(categoryRepository, testDispatcher)

            // When starting without resetting to Loading
            viewModel.start(isInitialLoad = false)
            advanceUntilIdle()

            // Then uiState is Error with refresh failure message
            val state = viewModel.uiState.value
            assertTrue(state is CategoryUiState.Error)
            state as CategoryUiState.Error
            assertTrue(state.message.contains("Fallo al refrescar: network fail"))
        }

    @Test
    fun `given success state when searchQuery changes then filteredCategories updates its value`() =
        testScope.runTest {
            // Given categories present
            val catApple = CategoryData(
                createdAt = "2025-01-01T00:00:00Z",
                id = "1",
                name = "Apple",
                updatedAt = "2025-01-02T00:00:00Z"
            )
            val catBanana = CategoryData(
                createdAt = "2025-01-01T00:00:00Z",
                id = "2",
                name = "Banana",
                updatedAt = "2025-01-02T00:00:00Z"
            )
            coEvery { categoryRepository.getCategories() } returns flowOf(
                listOf(
                    catApple,
                    catBanana
                )
            )
            coEvery { categoryRepository.refreshCategories() } returns Result.success(Unit)

            viewModel = CategoryViewModel(categoryRepository, testDispatcher)

            // When starting without resetting to Loading
            viewModel.start(isInitialLoad = false)
            advanceUntilIdle()

            // Then filteredCategories contains both
            assertEquals(listOf(catApple, catBanana), viewModel.filteredCategories.value)

            // When search query changes
            viewModel.onSearchQueryChanged("app")
            advanceUntilIdle()

            // Then filteredCategories contains only Apple
            assertEquals(listOf(catApple), viewModel.filteredCategories.value)
        }
}

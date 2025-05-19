package com.g18.ccp.repository.seller.order.category

import android.util.Log
import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.data.local.model.room.dao.CategoryDao
import com.g18.ccp.data.local.model.room.model.CategoryEntity
import com.g18.ccp.data.remote.model.seller.order.CategoryData
import com.g18.ccp.data.remote.model.seller.order.CategoryListResponse
import com.g18.ccp.data.remote.service.seller.order.category.CategoryService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SellerCategoryRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val categoryDao: CategoryDao = mockk()
    private val categoryService: CategoryService = mockk()
    private lateinit var repository: SellerCategoryRepositoryImpl

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any(), any<Throwable>()) } returns 0

        repository = SellerCategoryRepositoryImpl(categoryService, categoryDao)
    }

    @After
    fun tearDown() {
        unmockkAll()
        unmockkStatic(Log::class)
    }

    @Test
    fun `given dao emits entities when getCategories then maps to domain list`() = runTest {
        // Given two CategoryEntity from DAO
        val ent1 = CategoryEntity(
            createdAt = "2025-01-01T00:00:00Z",
            id = "1",
            name = "Cat1",
            updatedAt = "2025-01-02T00:00:00Z"
        )
        val ent2 = CategoryEntity(
            createdAt = "2025-01-03T00:00:00Z",
            id = "2",
            name = "Cat2",
            updatedAt = "2025-01-04T00:00:00Z"
        )
        coEvery { categoryDao.getAllCategories() } returns flowOf(listOf(ent1, ent2))

        // When collecting categories
        val result = repository.getCategories().first()

        // Then each entity is mapped to CategoryData with same fields
        assertEquals(2, result.size)
        assertTrue(
            result.contains(
                CategoryData(
                    createdAt = ent1.createdAt,
                    id = ent1.id,
                    name = ent1.name,
                    updatedAt = ent1.updatedAt
                )
            )
        )
        assertTrue(
            result.contains(
                CategoryData(
                    createdAt = ent2.createdAt,
                    id = ent2.id,
                    name = ent2.name,
                    updatedAt = ent2.updatedAt
                )
            )
        )
    }

    @Test
    fun `given dao emits null when getCategoryByCategoryId then returns null`() = runTest {
        // Given DAO returns null entity
        coEvery { categoryDao.getCategoryById("x") } returns flowOf(null)

        // When collecting single category
        val result = repository.getCategoryByCategoryId("x").first()

        // Then result is null
        assertNull(result)
    }

    @Test
    fun `given dao emits entity when getCategoryByCategoryId then maps to domain`() = runTest {
        // Given DAO returns entity
        val ent = CategoryEntity(
            createdAt = "2025-02-01T00:00:00Z",
            id = "42",
            name = "Food",
            updatedAt = "2025-02-02T00:00:00Z"
        )
        coEvery { categoryDao.getCategoryById("42") } returns flowOf(ent)

        // When collecting
        val result = repository.getCategoryByCategoryId("42").first()

        // Then maps to CategoryData
        assertEquals(
            CategoryData(
                createdAt = ent.createdAt,
                id = ent.id,
                name = ent.name,
                updatedAt = ent.updatedAt
            ), result
        )
    }

    @Test
    fun `given service returns data when refreshCategories then insertAll and return success`() =
        runTest {
            // Given remote data
            val cat1 = CategoryData(
                createdAt = "2025-03-01T00:00:00Z",
                id = "1",
                name = "X",
                updatedAt = "2025-03-02T00:00:00Z"
            )
            val cat2 = CategoryData(
                createdAt = "2025-03-03T00:00:00Z",
                id = "2",
                name = "Y",
                updatedAt = "2025-03-04T00:00:00Z"
            )
            val response = CategoryListResponse(code = 200, data = listOf(cat1, cat2))
            coEvery { categoryService.getCategories() } returns response
            val slotEntities = slot<List<CategoryEntity>>()
            coEvery { categoryDao.insertAll(capture(slotEntities)) } returns Unit

            // When refresh
            val result = repository.refreshCategories()
            advanceUntilIdle()

            // Then successful and DAO.insertAll called with mapped entities
            assertTrue(result.isSuccess)
            assertEquals(2, slotEntities.captured.size)
            assertEquals(cat1.id, slotEntities.captured[0].id)
            assertEquals(cat2.name, slotEntities.captured[1].name)
        }

    @Test
    fun `given service throws when refreshCategories then return failure and no insertAll`() =
        runTest {
            // Given service throws
            val ex = Exception("network")
            coEvery { categoryService.getCategories() } throws ex
            coEvery { categoryDao.insertAll(any()) } returns Unit

            // When refresh
            val result = repository.refreshCategories()
            advanceUntilIdle()

            // Then failure and DAO.insertAll not called
            assertTrue(result.isFailure)
            assertEquals(ex, result.exceptionOrNull())
            coVerify(exactly = 0) { categoryDao.insertAll(any()) }
        }
}

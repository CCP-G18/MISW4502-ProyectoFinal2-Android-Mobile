package com.g18.ccp.repository.seller.order.category.product

import android.util.Log
import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.data.local.model.room.dao.SellerProductDao
import com.g18.ccp.data.local.model.room.model.SellerProductEntity
import com.g18.ccp.data.local.model.room.model.toProductData
import com.g18.ccp.data.local.model.room.model.toSellerProductEntityList
import com.g18.ccp.data.remote.model.seller.order.product.SellerProductData
import com.g18.ccp.data.remote.model.seller.order.product.SellerProductListResponse
import com.g18.ccp.data.remote.service.seller.order.product.SellerProductService
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class SellerProductRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var service: SellerProductService
    private lateinit var dao: SellerProductDao
    private lateinit var repository: SellerProductRepositoryImpl

    @Before
    fun setUp() {
        service = mockk()
        dao = mockk()
        repository = SellerProductRepositoryImpl(service, dao, mainDispatcherRule.testDispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getProductsForCategoryFromDB maps entities to domain models`() = runTest {
        // Given
        val entity1 = SellerProductEntity(
            id = "p1",
            name = "Prod1",
            description = "Desc1",
            imageUrl = "url1",
            price = 100.0f,
            quantity = 5,
            categoryId = "c1",
            createdAt = "2025-01-01T00:00:00Z",
            updatedAt = "2025-01-01T00:00:00Z"
        )
        val entity2 = SellerProductEntity(
            id = "p2",
            name = "Prod2",
            description = "Desc2",
            imageUrl = "url2",
            price = 200.0f,
            quantity = 2,
            categoryId = "c1",
            createdAt = "2025-01-01T00:00:00Z",
            updatedAt = "2025-01-01T00:00:00Z"
        )
        every { dao.getProductsByCategory("c1") } returns flowOf(listOf(entity1, entity2))

        // When
        val result = repository.getProductsForCategoryFromDB("c1").first()

        // Then
        val expected = listOf(entity1.toProductData(), entity2.toProductData())
        assertEquals(expected, result)
    }

    @Test
    fun `getProductById returns domain when entity exists`() = runTest {
        // Given
        val entity = SellerProductEntity(
            id = "p1",
            name = "Prod1",
            description = "Desc1",
            imageUrl = "url1",
            price = 100.0f,
            quantity = 5,
            categoryId = "c1",
            createdAt = "2025-01-01T00:00:00Z",
            updatedAt = "2025-01-01T00:00:00Z"
        )
        coEvery { dao.getProductById("p1") } returns entity

        // When
        val result = repository.getProductById("p1")

        // Then
        assertEquals(entity.toProductData(), result)
    }

    @Test
    fun `getProductById returns null when dao returns null`() = runTest {
        // Given
        coEvery { dao.getProductById("nope") } returns null

        // When
        val result = repository.getProductById("nope")

        // Then
        assertNull(result)
    }

    @Test
    fun `refreshProductsForCategory on successful API inserts and returns success`() = runTest {
        // Given
        val remote = SellerProductData(
            id = "p1",
            categoryId = "c1",
            createdAt = "2025-01-01T00:00:00Z",
            description = "Desc1",
            imageUrl = "url1",
            name = "Prod1",
            quantity = 5,
            price = 100.0f,
            updatedAt = "2025-01-02T00:00:00Z"
        )
        val response = SellerProductListResponse(
            code = 200,
            data = listOf(remote),
            message = "OK",
            status = "success"
        )
        coEvery { service.getProducts("c1") } returns Response.success(response)
        val entities = response.data.toSellerProductEntityList()
        coEvery { dao.insertAllProducts(entities) } just Runs

        // When
        val result = repository.refreshProductsForCategory("c1", "cust1")

        // Then
        coVerify { dao.insertAllProducts(entities) }
        assertTrue(result.isSuccess)
    }

    @Test
    fun `refreshProductsForCategory on HTTP error returns failure`() = runTest {
        // Given
        val errorBody = "bad request"
        val resp: Response<SellerProductListResponse> = Response.error(
            400,
            errorBody.toResponseBody("text/plain".toMediaType())
        )
        coEvery { service.getProducts("c1") } returns resp

        // When
        val result = repository.refreshProductsForCategory("c1", "cust1")

        // Then
        coVerify(exactly = 0) { dao.insertAllProducts(any()) }
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()!!.message!!.contains("API Error"))
    }

    @Test
    fun `refreshProductsForCategory on exception returns failure`() = runTest {
        // Given
        val ex = RuntimeException("network down")
        coEvery { service.getProducts("c1") } throws ex

        // When
        val result = repository.refreshProductsForCategory("c1", "cust1")

        // Then
        coVerify(exactly = 0) { dao.insertAllProducts(any()) }
        assertTrue(result.isFailure)
        assertSame(ex, result.exceptionOrNull())
    }
}
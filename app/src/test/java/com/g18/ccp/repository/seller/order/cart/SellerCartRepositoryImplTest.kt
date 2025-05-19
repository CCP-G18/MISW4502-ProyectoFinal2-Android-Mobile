package com.g18.ccp.repository.seller.order.cart

import android.util.Log
import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.data.local.model.cart.seller.SellerCartItemWithProduct
import com.g18.ccp.data.local.model.room.dao.SellerCartDao
import com.g18.ccp.data.local.model.room.model.SellerCartItemEntity
import com.g18.ccp.data.remote.model.seller.order.product.SellerProductData
import com.g18.ccp.repository.seller.order.category.product.SellerProductRepository
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException


@ExperimentalCoroutinesApi
class SellerCartRepositoryImplTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var cartDao: SellerCartDao

    @MockK
    private lateinit var productRepository: SellerProductRepository

    private lateinit var repository: SellerCartRepositoryImpl

    private val testProductId = "prod1"
    private val testCustomerId = "cust1" // Though not used in this cart version

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>(), any()) } returns 0
        repository =
            SellerCartRepositoryImpl(cartDao, productRepository, mainDispatcherRule.testDispatcher)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    private fun createFakeSellerProductData(id: String, stock: Int, price: Float = 10f) =
        SellerProductData(
            id = id,
            categoryId = "cat1",
            createdAt = "",
            description = "Desc $id",
            imageUrl = "url",
            name = "Product $id",
            quantity = stock,
            price = price,
            updatedAt = ""
        )

    private fun createFakeSellerCartItemEntity(productId: String, quantity: Int) =
        SellerCartItemEntity(
            productId = productId, quantityInCart = quantity
        )

    private fun createFakeSellerCartItemWithProduct(
        productId: String,
        quantityInCart: Int,
        name: String,
        stock: Int,
        price: Float
    ) = SellerCartItemWithProduct(
        productId = productId,
        quantityInCart = quantityInCart,
        name = name,
        description = "Desc $productId",
        price = price,
        imageUrl = "url",
        stock = stock
    )


    @Test
    fun `getCartItemsWithDetails - given Dao Returns Items - then Returns Mapped SellerCartItem Flow`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fakeDaoItem = createFakeSellerCartItemWithProduct(
                testProductId,
                2,
                "Product $testProductId",
                10,
                10f
            )
            every { cartDao.getAllCartItemsWithDetails() } returns flowOf(listOf(fakeDaoItem))

            val resultFlow = repository.getCartItemsWithDetails()
            advanceUntilIdle()
            val resultList = resultFlow.first()

            assertEquals(1, resultList.size)
            assertEquals(testProductId, resultList[0].product.id)
            assertEquals(2, resultList[0].quantity)
            verify(exactly = 1) { cartDao.getAllCartItemsWithDetails() }
        }

    @Test
    fun `addItem - given Valid Product And Quantity And Item Not In Cart - then Inserts Item And Returns Success`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val product = createFakeSellerProductData(testProductId, 10)
            val quantityToAdd = 2
            coEvery { cartDao.getCartItemByProductId(testProductId) } returns null
            coEvery { cartDao.insertOrUpdateItem(any()) } just runs

            val result = repository.addItem(product, quantityToAdd)
            advanceUntilIdle()

            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { cartDao.getCartItemByProductId(testProductId) }
            coVerify(exactly = 1) { cartDao.insertOrUpdateItem(match { it.productId == testProductId && it.quantityInCart == quantityToAdd }) }
        }

    @Test
    fun `addItem - given Item Exists And Quantity Update Valid - then Updates Item And Returns Success`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val product = createFakeSellerProductData(testProductId, 10)
            val existingItemEntity = createFakeSellerCartItemEntity(testProductId, 1)
            val quantityToAdd = 2
            val expectedFinalQuantity = 3
            coEvery { cartDao.getCartItemByProductId(testProductId) } returns existingItemEntity
            coEvery { cartDao.insertOrUpdateItem(any()) } just runs

            val result = repository.addItem(product, quantityToAdd)
            advanceUntilIdle()

            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { cartDao.insertOrUpdateItem(match { it.productId == testProductId && it.quantityInCart == expectedFinalQuantity }) }
        }

    @Test
    fun `addItem - given Quantity Exceeds Stock - then Returns Failure`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val product = createFakeSellerProductData(testProductId, 5)
            val quantityToAdd = 6

            val result = repository.addItem(product, quantityToAdd)
            advanceUntilIdle()

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            assertEquals("Cantidad excede stock", result.exceptionOrNull()?.message)
        }

    @Test
    fun `addItem - given Total Quantity Exceeds Stock - then Returns Failure`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val product = createFakeSellerProductData(testProductId, 5)
            val existingItemEntity = createFakeSellerCartItemEntity(testProductId, 3)
            val quantityToAdd = 3
            coEvery { cartDao.getCartItemByProductId(testProductId) } returns existingItemEntity

            val result = repository.addItem(product, quantityToAdd)
            advanceUntilIdle()

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            assertEquals(
                "Cantidad total en carrito excede stock.",
                result.exceptionOrNull()?.message
            )
        }


    @Test
    fun `updateItemQuantity - given Valid New Quantity Is Zero - then Deletes Item And Returns Success`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val itemInCart = createFakeSellerCartItemEntity(testProductId, 2)
            val productDetails = createFakeSellerProductData(testProductId, 10)
            coEvery { cartDao.getCartItemByProductId(testProductId) } returns itemInCart
            coEvery { productRepository.getProductById(testProductId) } returns productDetails
            coEvery { cartDao.deleteItem(testProductId) } just runs

            val result = repository.updateItemQuantity(testProductId, 0)
            advanceUntilIdle()

            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { cartDao.deleteItem(testProductId) }
            coVerify(exactly = 0) { cartDao.insertOrUpdateItem(any()) }
        }

    @Test
    fun `updateItemQuantity - given Valid New Positive Quantity - then Updates Item And Returns Success`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val itemInCart = createFakeSellerCartItemEntity(testProductId, 2)
            val productDetails = createFakeSellerProductData(testProductId, 10)
            val newQuantity = 5
            coEvery { cartDao.getCartItemByProductId(testProductId) } returns itemInCart
            coEvery { productRepository.getProductById(testProductId) } returns productDetails
            coEvery { cartDao.insertOrUpdateItem(any()) } just runs

            val result = repository.updateItemQuantity(testProductId, newQuantity)
            advanceUntilIdle()

            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { cartDao.insertOrUpdateItem(match { it.productId == testProductId && it.quantityInCart == newQuantity }) }
        }

    @Test
    fun `updateItemQuantity - given New Quantity Exceeds Stock - then Returns Failure`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val itemInCart = createFakeSellerCartItemEntity(testProductId, 2)
            val productDetails = createFakeSellerProductData(testProductId, 5)
            val newQuantity = 6
            coEvery { cartDao.getCartItemByProductId(testProductId) } returns itemInCart
            coEvery { productRepository.getProductById(testProductId) } returns productDetails

            val result = repository.updateItemQuantity(testProductId, newQuantity)
            advanceUntilIdle()

            assertTrue(result.isFailure)
            assertEquals("Cantidad excede stock", result.exceptionOrNull()?.message)
        }

    @Test
    fun `updateItemQuantity - given Item Not In Cart - then Returns Failure`() =
        runTest(mainDispatcherRule.testDispatcher) {
            coEvery { cartDao.getCartItemByProductId(testProductId) } returns null

            val result = repository.updateItemQuantity(testProductId, 1)
            advanceUntilIdle()

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is NoSuchElementException)
        }

    @Test
    fun `updateItemQuantity - given Product Details Not Found - then Returns Failure`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val itemInCart = createFakeSellerCartItemEntity(testProductId, 2)
            coEvery { cartDao.getCartItemByProductId(testProductId) } returns itemInCart
            coEvery { productRepository.getProductById(testProductId) } returns null

            val result = repository.updateItemQuantity(testProductId, 1)
            advanceUntilIdle()

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is NoSuchElementException)
            assertEquals("Detalles del producto no encontrados", result.exceptionOrNull()?.message)
        }


    @Test
    fun `removeItem - given ProductId - then Calls DaoDeleteItem And Returns Success`() =
        runTest(mainDispatcherRule.testDispatcher) {
            coEvery { cartDao.deleteItem(testProductId) } just runs

            val result = repository.removeItem(testProductId)
            advanceUntilIdle()

            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { cartDao.deleteItem(testProductId) }
        }

    @Test
    fun `removeItem - given DaoThrowsException - then ReturnsFailure`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val exception = IOException("DB Error")
            coEvery { cartDao.deleteItem(testProductId) } throws exception

            val result = repository.removeItem(testProductId)
            advanceUntilIdle()

            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }

    @Test
    fun `clearCart - whenCalled - then CallsDaoClearCart And ReturnsSuccess`() =
        runTest(mainDispatcherRule.testDispatcher) {
            coEvery { cartDao.clearCart() } just runs

            val result = repository.clearCart()
            advanceUntilIdle()

            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { cartDao.clearCart() }
        }

    @Test
    fun `clearCart - givenDaoThrowsException - then ReturnsFailure`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val exception = IOException("DB Error")
            coEvery { cartDao.clearCart() } throws exception

            val result = repository.clearCart()
            advanceUntilIdle()

            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
        }
}

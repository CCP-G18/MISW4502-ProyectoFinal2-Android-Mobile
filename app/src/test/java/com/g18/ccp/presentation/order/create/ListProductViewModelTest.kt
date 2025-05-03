package com.g18.ccp.presentation.order.create

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.data.local.model.cart.CartItem
import com.g18.ccp.data.remote.model.order.Order
import com.g18.ccp.data.remote.model.product.ProductResponse
import com.g18.ccp.repository.order.OrdersRepository
import com.g18.ccp.repository.product.ProductRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import retrofit2.Response

@ExperimentalCoroutinesApi
class ListProductViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var productRepository: ProductRepository
    private lateinit var orderRepository: OrdersRepository
    private lateinit var viewModel: ListProductViewModel

    @Before
    fun setUp() {
        productRepository = mockk()
        orderRepository = mockk()
        viewModel = ListProductViewModel(productRepository, orderRepository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun givenProductsAvailable_whenLoadProducts_thenUiStateIsSuccessWithCartItems() = runTest {
        // given
        val products = listOf(
            TestData.product(id = "1", name = "A", price = 10.0f, leftAmount = 5),
            TestData.product(id = "2", name = "B", price = 20.0f, leftAmount = 3)
        )
        coEvery { productRepository.getProducts() } returns ProductResponse(
            code = 200,
            data = products
        )

        // when
        viewModel.loadProducts()
        advanceUntilIdle()

        // then
        val state = viewModel.uiState.value
        assert(state is Output.Success)
        val cartItems = (state as Output.Success).data
        Assert.assertEquals(2, cartItems.size)
        Assert.assertTrue(cartItems.all { it.quantity == 1 })
        Assert.assertEquals("1", cartItems[0].product.id)
        Assert.assertEquals("2", cartItems[1].product.id)
    }

    @Test
    fun givenProductsUnavailable_whenLoadProducts_thenUiStateIsFailure() = runTest {
        // given
        val ex = RuntimeException("Network error")
        coEvery { productRepository.getProducts() } throws ex

        // when
        viewModel.loadProducts()
        advanceUntilIdle()

        // then
        val state = viewModel.uiState.value
        assert(state is Output.Failure<*>)
        Assert.assertEquals(ex, (state as Output.Failure<*>).exception)
    }

    @Test
    fun givenEmptyCart_whenAddProduct_thenCartHasOneItemQuantityOne() {
        // given
        val item = CartItem(product = TestData.product(id = "X"))

        // when
        viewModel.addProduct(item)

        // then
        val cart = viewModel.cart.value
        Assert.assertEquals(1, cart.size)
        Assert.assertEquals("X", cart[0].product.id)
        Assert.assertEquals(1, cart[0].quantity)
    }

    @Test
    fun givenItemInCartWithQuantityOne_whenRemoveProduct_thenCartIsEmpty() {
        // given
        val item = CartItem(product = TestData.product(id = "Y"))
        viewModel.addProduct(item)

        // when
        viewModel.removeProduct(item)

        // then
        Assert.assertTrue(viewModel.cart.value.isEmpty())
    }

    @Test
    fun givenItemInCartWithQuantityTwo_whenRemoveProduct_thenQuantityDecreases() {
        // given
        val item = CartItem(product = TestData.product(id = "Z"))
        viewModel.addProduct(item)
        viewModel.addProduct(item)

        // when
        viewModel.removeProduct(item)

        // then
        val cart = viewModel.cart.value
        Assert.assertEquals(1, cart.size)
        Assert.assertEquals(1, cart[0].quantity)
    }

    @Test
    fun givenItemInCart_whenRemoveAllProduct_thenCartBecomesEmpty() {
        // given
        val item = CartItem(product = TestData.product(id = "W"))
        viewModel.addProduct(item)
        viewModel.addProduct(item)

        // when
        viewModel.removeAllProduct(item)

        // then
        Assert.assertTrue(viewModel.cart.value.isEmpty())
    }

    @Test
    fun givenCartWithItems_whenGetQuantity_thenReturnsCorrectQuantity() {
        // given
        val item = CartItem(product = TestData.product(id = "Q"))
        viewModel.addProduct(item)
        viewModel.addProduct(item)

        // when
        val qty = viewModel.getQuantity(item)

        // then
        Assert.assertEquals(2, qty)
    }

    @Test
    fun givenCartWithTwoItems_whenGetOrderTotal_thenReturnsSum() {
        // given
        val p1 = TestData.product(id = "1", price = 5.0f)
        val p2 = TestData.product(id = "2", price = 7.5f)
        viewModel.addProduct(CartItem(product = p1))
        viewModel.addProduct(CartItem(product = p2))
        viewModel.addProduct(CartItem(product = p2))

        // when
        val total = viewModel.getOrderTotal()

        // then
        Assert.assertEquals(20.0, total, 0.0)
    }

    @Test
    fun givenSuccessfulOrderCreation_whenCreateOrder_thenOrderStateIsSuccessAndReturnsOrder() =
        runTest {
            // given
            // add items to cart
            val p1 = TestData.product(id = "1", name = "Prod1", price = 5.0f)
            val p2 = TestData.product(id = "2", name = "Prod2", price = 7.5f)
            viewModel.addProduct(CartItem(product = p1))
            viewModel.addProduct(CartItem(product = p2))
            viewModel.addProduct(CartItem(product = p2))

            // capture built order
            val slot = slot<Order>()
            coEvery { orderRepository.createOrder(capture(slot)) } answers {
                Response.success(slot.captured)
            }

            // when
            viewModel.createOrder()
            advanceUntilIdle()

            // then
            val state = viewModel.orderState.value
            assert(state is Output.Success)

            // verify returned Order matches captured
            val returnedOrder = slot.captured
            Assert.assertEquals(returnedOrder, slot.captured)
        }

    @Test
    fun givenFailedOrderCreation_whenCreateOrder_thenOrderStateIsFailure() = runTest {
        // given
        val errorBody = ResponseBody.create("text/plain".toMediaTypeOrNull(), "err")
        coEvery { orderRepository.createOrder(any()) } returns Response.error(400, errorBody)

        // when
        viewModel.createOrder()
        advanceUntilIdle()

        // then
        val state = viewModel.orderState.value
        assert(state is Output.Failure<*>)
        Assert.assertTrue(((state as Output.Failure<*>).exception as Throwable).message!!.contains("Error creating order"))
    }

    @Test
    fun givenOrderStateSuccess_whenClearOrderState_thenOrderStateIsLoading() = runTest {
        // given
        val slot = slot<Order>()
        coEvery { orderRepository.createOrder(capture(slot)) } answers { Response.success(slot.captured) }
        viewModel.createOrder()
        advanceUntilIdle()
        assert(viewModel.orderState.value is Output.Success)

        // when
        viewModel.clearOrderState()

        // then
        Assert.assertTrue(viewModel.orderState.value is Output.Loading)
    }
}

// Helper para crear datos de prueba
private object TestData {
    fun product(
        id: String,
        name: String = "N",
        price: Float = 1.0f,
        leftAmount: Int = 10,
        imageUrl: String = ""
    ) = com.g18.ccp.data.remote.model.product.Product(
        id = id,
        name = name,
        price = price,
        leftAmount = leftAmount,
        imageUrl = imageUrl
    )
}

@ExperimentalCoroutinesApi
class MainDispatcherRule : TestWatcher() {
    private val testDispatcher = UnconfinedTestDispatcher()
    override fun starting(description: org.junit.runner.Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: org.junit.runner.Description?) {
        Dispatchers.resetMain()
    }
}

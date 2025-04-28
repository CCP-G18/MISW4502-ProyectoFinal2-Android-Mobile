package com.g18.ccp.repository.product

import com.g18.ccp.data.remote.model.product.Product
import com.g18.ccp.data.remote.model.product.ProductResponse
import com.g18.ccp.data.remote.service.product.ProductService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ProductRepositoryImplTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var productService: ProductService

    private lateinit var productRepository: ProductRepositoryImpl

    @Before
    fun setUp() {
        productRepository = ProductRepositoryImpl(productService)
    }

    @Test
    fun `given Successful ProductService Call - when getProducts is Called - then Returns Expected ProductResponse`() =
        runTest {
            val fakeProductList = listOf(
                Product(
                    id = "p1",
                    name = "Product 1",
                    leftAmount = 10,
                    price = 100.0f,
                    imageUrl = "url1"
                ),
                Product(id = "p2", name = "Product 2", leftAmount = 5, price = 25.5f)
            )
            val expectedResponse = ProductResponse(code = 200, data = fakeProductList)
            coEvery { productService.getProducts() } returns expectedResponse

            val actualResponse = productRepository.getProducts()

            assertEquals(expectedResponse, actualResponse)
            coVerify(exactly = 1) { productService.getProducts() }
        }
}

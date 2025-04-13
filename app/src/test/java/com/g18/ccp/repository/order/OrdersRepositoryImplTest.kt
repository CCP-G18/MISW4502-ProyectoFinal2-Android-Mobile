package com.g18.ccp.repository.order

import com.g18.ccp.core.constants.enums.OrderStatus
import com.g18.ccp.data.remote.model.order.Order
import com.g18.ccp.data.remote.model.order.OrderItem
import com.g18.ccp.data.remote.service.order.OrderService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersRepositoryImplTest {

    private lateinit var service: OrderService
    private lateinit var repository: OrdersRepository

    @Before
    fun setUp() {
        service = mockk()
        repository = OrdersRepositoryImpl(service)
    }

    @Test
    fun `given service returns order list when getOrders is called then return expected list`() =
        runTest {
            // Given
            val expectedOrders = listOf(
                Order(
                    id = "123",
                    summary = "Leche, Pan...",
                    date = "2025-01-01",
                    total = 150000f,
                    status = OrderStatus.DELIVERED,
                    items = listOf(
                        OrderItem(
                            title = "Leche",
                            quantity = 1,
                            price = 3000f,
                            imageUrl = "https://example.com/leche.jpg"
                        )
                    )
                )
            )
            coEvery { service.getOrders() } returns expectedOrders

            // When
            val result = repository.getOrders()

            // Then
            assertEquals(expectedOrders, result)
        }

    @Test
    fun `given service throws exception when getOrders is called then exception is thrown`() =
        runTest {
            // Given
            val expectedMessage = "Network error"
            coEvery { service.getOrders() } throws Exception(expectedMessage)

            // When
            try {
                repository.getOrders()
                assert(false) { "Expected an exception but none was thrown" }
            } catch (e: Exception) {
                // Then
                assertEquals(expectedMessage, e.message)
            }
        }
}

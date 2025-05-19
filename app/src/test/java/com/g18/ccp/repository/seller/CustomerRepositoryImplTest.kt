package com.g18.ccp.repository.seller

import android.util.Log
import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.core.constants.enums.IdentificationType
import com.g18.ccp.core.utils.mapper.toCustomerEntityList
import com.g18.ccp.core.utils.mapper.toDomainCustomerModel
import com.g18.ccp.core.utils.mapper.toDomainCustomerModelList
import com.g18.ccp.data.local.model.room.dao.CustomerDao
import com.g18.ccp.data.local.model.room.model.CustomerEntity
import com.g18.ccp.data.remote.model.seller.CustomerData
import com.g18.ccp.data.remote.model.seller.CustomerListResponse
import com.g18.ccp.data.remote.service.seller.CustomerService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class CustomerRepositoryImplTest {

    @get:Rule
    val mockkRule = io.mockk.junit4.MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var customerService: CustomerService
    private lateinit var customerDao: CustomerDao
    private lateinit var repository: CustomerRepositoryImpl

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        customerService = mockk()
        customerDao = mockk()
        // Repository doesn't need context directly if using DI for Dao/Service which handle context
        repository = CustomerRepositoryImpl(customerService, customerDao)

        // Mock mapper static functions if they are top-level or in objects
        mockkStatic("com.g18.ccp.core.utils.mapper.MappersKt")
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        unmockkStatic("com.g18.ccp.core.utils.mapper.MappersKt")
    }

    @Test
    fun `given Dao Returns Entities - when getCustomers is Called - then Returns Mapped CustomerData Flow`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fakeEntityList = listOf(TestData.entity("1"), TestData.entity("2"))
            val expectedDataList = listOf(TestData.customerData("1"), TestData.customerData("2"))
            every { customerDao.getAllCustomers() } returns flowOf(fakeEntityList)
            every { fakeEntityList.toDomainCustomerModelList() } returns expectedDataList

            val resultFlow = repository.getCustomers()
            advanceUntilIdle()
            val resultData = resultFlow.first()

            assertEquals(expectedDataList, resultData)
            verify(exactly = 1) { customerDao.getAllCustomers() }
            verify(exactly = 1) { fakeEntityList.toDomainCustomerModelList() }
        }

    @Test
    fun `given Dao Returns Empty List - when getCustomers is Called - then Returns Empty Flow`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val emptyEntityList = emptyList<CustomerEntity>()
            val expectedDataList = emptyList<CustomerData>()
            every { customerDao.getAllCustomers() } returns flowOf(emptyEntityList)
            every { emptyEntityList.toDomainCustomerModelList() } returns expectedDataList

            val resultFlow = repository.getCustomers()
            advanceUntilIdle()
            val resultData = resultFlow.first()


            assertEquals(expectedDataList, resultData)
            verify(exactly = 1) { customerDao.getAllCustomers() }
            verify(exactly = 1) { emptyEntityList.toDomainCustomerModelList() }
        }


    @Test
    fun `given Dao Returns Entity - when getCustomerById is Called - then Returns Mapped CustomerData Flow`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val customerId = "cust1"
            val fakeEntity = TestData.entity(customerId)
            val expectedData = TestData.customerData(customerId)
            every { customerDao.getCustomerById(customerId) } returns flowOf(fakeEntity)
            every { fakeEntity.toDomainCustomerModel() } returns expectedData

            val resultFlow = repository.getCustomerById(customerId)
            advanceUntilIdle()
            val resultData = resultFlow.first()

            assertEquals(expectedData, resultData)
            verify(exactly = 1) { customerDao.getCustomerById(customerId) }
            verify(exactly = 1) { fakeEntity.toDomainCustomerModel() }
        }

    @Test
    fun `given Dao Returns Null - when getCustomerById is Called - then Returns Null Flow`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val customerId = "custNotFound"
            every { customerDao.getCustomerById(customerId) } returns flowOf(null)

            val resultFlow = repository.getCustomerById(customerId)
            advanceUntilIdle()
            val resultData = resultFlow.first()

            assertNull(resultData)
            verify(exactly = 1) { customerDao.getCustomerById(customerId) }
        }


    @Test
    fun `given Service Succeeds - when refreshCustomers is Called - then Calls Dao InsertAll And Returns Success`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fakeDataList = listOf(TestData.customerData("1"))
            val fakeEntityList = listOf(TestData.entity("1"))
            val fakeResponse = CustomerListResponse(
                code = 200,
                data = fakeDataList,
                message = "OK",
                status = "success"
            ) // Use user's class structure
            coEvery { customerService.getCustomers() } returns fakeResponse
            every { fakeDataList.toCustomerEntityList() } returns fakeEntityList
            coEvery { customerDao.insertAll(fakeEntityList) } just runs

            val result = repository.refreshCustomers()
            advanceUntilIdle()

            assertTrue(result.isSuccess)
            coVerify(exactly = 1) { customerService.getCustomers() }
            verify(exactly = 1) { fakeDataList.toCustomerEntityList() }
            coVerify(exactly = 1) { customerDao.insertAll(fakeEntityList) }
        }

    @Test
    fun `given Service Returns NonSuccess Status - when refreshCustomers is Called - then Returns Failure And Does Not Call Dao`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fakeResponse = CustomerListResponse(
                code = 400,
                data = emptyList(),
                message = "Bad Request",
                status = "error"
            ) // Use user's class structure
            coEvery { customerService.getCustomers() } returns fakeResponse

            val result = repository.refreshCustomers()
            advanceUntilIdle()

            assertTrue(result.isFailure)
            assertEquals("API Error 400: Bad Request", result.exceptionOrNull()?.message)
            coVerify(exactly = 1) { customerService.getCustomers() }
            verify(exactly = 0) { any<List<CustomerData>>().toCustomerEntityList() }
            coVerify(exactly = 0) { customerDao.insertAll(any()) }
        }

    @Test
    fun `given Service Throws IOException - when refreshCustomers is Called - then Returns Failure And Does Not Call Dao`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val expectedException = IOException("Network Error")
            coEvery { customerService.getCustomers() } throws expectedException

            val result = repository.refreshCustomers()
            advanceUntilIdle()

            assertTrue(result.isFailure)
            assertEquals(expectedException, result.exceptionOrNull())
            coVerify(exactly = 1) { customerService.getCustomers() }
            verify(exactly = 0) { any<List<CustomerData>>().toCustomerEntityList() }
            coVerify(exactly = 0) { customerDao.insertAll(any()) }
        }


    @Test
    fun `given Service Succeeds But Dao Insert Fails - when refreshCustomers is Called - then Returns Failure`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fakeDataList = listOf(TestData.customerData("1"))
            val fakeEntityList = listOf(TestData.entity("1"))
            val fakeResponse = CustomerListResponse(
                code = 200,
                data = fakeDataList,
                message = "OK",
                status = "success"
            )
            val dbException = RuntimeException("DB Insert Failed")
            coEvery { customerService.getCustomers() } returns fakeResponse
            every { fakeDataList.toCustomerEntityList() } returns fakeEntityList
            coEvery { customerDao.insertAll(fakeEntityList) } throws dbException

            val result = repository.refreshCustomers()
            advanceUntilIdle()

            assertTrue(result.isFailure)
            assertEquals(dbException, result.exceptionOrNull())
            coVerify(exactly = 1) { customerService.getCustomers() }
            verify(exactly = 1) { fakeDataList.toCustomerEntityList() }
            coVerify(exactly = 1) { customerDao.insertAll(fakeEntityList) }
        }
}


private object TestData {
    fun customerData(id: String, name: String = "Test Name $id") = CustomerData(
        address = "Address $id", city = "City", country = "Country",
        email = "email$id@example.com", id = id, identificationNumber = "ID$id",
        identificationType = IdentificationType.CC, name = name
    )

    fun entity(id: String, name: String = "Test Name $id") = CustomerEntity(
        address = "Address $id", city = "City", country = "Country",
        email = "email$id@example.com", id = id, identificationNumber = "ID$id",
        identificationType = IdentificationType.CC, name = name
    )
}

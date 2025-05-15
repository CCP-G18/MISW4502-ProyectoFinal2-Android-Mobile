package com.g18.ccp.repository.seller.customervisit

import android.util.Log
import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.core.session.UserSessionManager
import com.g18.ccp.data.local.Datasource
import com.g18.ccp.data.remote.model.auth.UserInfo
import com.g18.ccp.data.remote.model.seller.visits.VisitData
import com.g18.ccp.data.remote.model.seller.visits.VisitListResponse
import com.g18.ccp.data.remote.model.seller.visits.registervisit.RegisterVisitRequest
import com.g18.ccp.data.remote.model.seller.visits.registervisit.RegisterVisitResponse
import com.g18.ccp.data.remote.service.seller.visits.VisitService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import java.io.IOException
import java.util.Date

@ExperimentalCoroutinesApi
class VisitRepositoryImplTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var datasource: Datasource

    @MockK
    private lateinit var visitApiService: VisitService

    private lateinit var repository: VisitRepositoryImpl

    private val testCustomerId = "cust123"
    private val testSellerId = "seller456"
    private val testDateString = "2025-05-14"
    private val testObservations = "Test observation"

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.e(any(), any<String>(), any()) } returns 0

        mockkObject(UserSessionManager)
        repository =
            VisitRepositoryImpl(mainDispatcherRule.testDispatcher, datasource, visitApiService)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        unmockkObject(UserSessionManager)
    }

    private fun createFakeUserInfo(id: String = testSellerId) =
        UserInfo(id = id, email = "seller@example.com", role = "seller", username = "sellerUser")

    private fun createFakeVisitData(id: String) = VisitData(
        id = id,
        observations = "obs",
        customerId = testCustomerId,
        sellerId = testSellerId,
        registerDate = Date()
    )

    private fun createFakeVisitListResponse(data: List<VisitData>) =
        VisitListResponse(code = 200, data = data, message = "Success", status = "success")

    private fun createFakeRegisterVisitResponse() =
        RegisterVisitResponse(code = 201, message = "Created", status = "success")

    @Test
    fun `getVisitsForCustomer - given UserSessionManager Returns Null SellerId - then Returns Failure`() =
        runTest(mainDispatcherRule.testDispatcher) {
            coEvery { UserSessionManager.getUserInfo(datasource) } returns null

            val result = repository.getVisitsForCustomer(testCustomerId)
            advanceUntilIdle()

            assertTrue(result.isFailure)
            assertEquals("Seller ID is null", result.exceptionOrNull()?.message)
            coVerify(exactly = 1) { UserSessionManager.getUserInfo(datasource) }
            coVerify(exactly = 0) { visitApiService.getVisits(any()) }
        }

    @Test
    fun `getVisitsForCustomer - given Service Succeeds - then Returns Success With VisitData List`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fakeVisits = listOf(createFakeVisitData("visit1"))
            val fakeResponse = Response.success(createFakeVisitListResponse(fakeVisits))
            coEvery { UserSessionManager.getUserInfo(datasource) } returns createFakeUserInfo()
            coEvery { visitApiService.getVisits(testCustomerId/*, testSellerId*/) } returns fakeResponse // Adjust if your service takes sellerId

            val result = repository.getVisitsForCustomer(testCustomerId)
            advanceUntilIdle()

            assertTrue(result.isSuccess)
            assertEquals(fakeVisits, result.getOrNull())
            coVerify(exactly = 1) { UserSessionManager.getUserInfo(datasource) }
            coVerify(exactly = 1) { visitApiService.getVisits(testCustomerId/*, testSellerId*/) }
        }

    @Test
    fun `getVisitsForCustomer - given Service Fails With ErrorBody - then Returns Failure`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val errorResponseBody =
                "{\"error\":\"Not found\"}".toResponseBody("application/json".toMediaTypeOrNull())
            val fakeResponse = Response.error<VisitListResponse>(404, errorResponseBody)
            coEvery { UserSessionManager.getUserInfo(datasource) } returns createFakeUserInfo()
            coEvery { visitApiService.getVisits(testCustomerId/*, testSellerId*/) } returns fakeResponse

            val result = repository.getVisitsForCustomer(testCustomerId)
            advanceUntilIdle()

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull()?.message?.contains("Error 404") == true)
            coVerify(exactly = 1) { UserSessionManager.getUserInfo(datasource) }
            coVerify(exactly = 1) { visitApiService.getVisits(testCustomerId/*, testSellerId*/) }
        }

    @Test
    fun `getVisitsForCustomer - given Service Throws IOException - then Returns Failure`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val exception = IOException("Network issue")
            coEvery { UserSessionManager.getUserInfo(datasource) } returns createFakeUserInfo()
            coEvery { visitApiService.getVisits(testCustomerId/*, testSellerId*/) } throws exception

            val result = repository.getVisitsForCustomer(testCustomerId)
            advanceUntilIdle()

            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            coVerify(exactly = 1) { UserSessionManager.getUserInfo(datasource) }
            coVerify(exactly = 1) { visitApiService.getVisits(testCustomerId/*, testSellerId*/) }
        }

    @Test
    fun `registerVisit - given Service Succeeds - then Returns Success With Response`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val fakeRequest =
                RegisterVisitRequest(testCustomerId, testSellerId, testDateString, testObservations)
            val fakeApiResponse = createFakeRegisterVisitResponse()
            val fakeResponse = Response.success(fakeApiResponse)
            coEvery { UserSessionManager.getUserInfo(datasource) } returns createFakeUserInfo(
                testSellerId
            )
            coEvery { visitApiService.registerVisit(any()) } returns fakeResponse

            val result = repository.registerVisit(testCustomerId, testDateString, testObservations)
            advanceUntilIdle()

            assertTrue(result.isSuccess)
            assertEquals(fakeApiResponse, result.getOrNull())
            coVerify(exactly = 1) { UserSessionManager.getUserInfo(datasource) }
            coVerify(exactly = 1) { visitApiService.registerVisit(any()) }
        }

    @Test
    fun `registerVisitgiven UserSessionManager Returns Nullthen Service Called With Empty SellerId And Handles Response`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val registerVisitRequestSlot = slot<RegisterVisitRequest>()
            val fakeApiResponse = createFakeRegisterVisitResponse()
            val fakeResponse = Response.success(fakeApiResponse)
            coEvery { UserSessionManager.getUserInfo(datasource) } returns null
            coEvery { visitApiService.registerVisit(capture(registerVisitRequestSlot)) } returns fakeResponse

            val result = repository.registerVisit(testCustomerId, testDateString, testObservations)
            advanceUntilIdle()

            assertTrue(result.isSuccess)
            assertEquals(fakeApiResponse, result.getOrNull())
            coVerify(exactly = 1) { UserSessionManager.getUserInfo(datasource) }
            coVerify(exactly = 1) { visitApiService.registerVisit(registerVisitRequestSlot.captured) }
        }

    @Test
    fun `registerVisit - given Service Fails With ErrorBody - then Returns Failure`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val registerVisitRequestSlot = slot<RegisterVisitRequest>()
            val errorResponseBody =
                "{\"error\":\"Bad request\"}".toResponseBody("application/json".toMediaTypeOrNull())
            val fakeResponse = Response.error<RegisterVisitResponse>(400, errorResponseBody)
            coEvery { UserSessionManager.getUserInfo(datasource) } returns createFakeUserInfo(
                testSellerId
            )
            coEvery { visitApiService.registerVisit(capture(registerVisitRequestSlot)) } returns fakeResponse

            val result = repository.registerVisit(testCustomerId, testDateString, testObservations)
            advanceUntilIdle()

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull()?.message?.contains("Error 400") == true)
            coVerify(exactly = 1) { UserSessionManager.getUserInfo(datasource) }
            coVerify(exactly = 1) { visitApiService.registerVisit(registerVisitRequestSlot.captured) }
        }

    @Test
    fun `registerVisit - given Service Throws IOException - then Returns Failure`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val registerVisitRequestSlot = slot<RegisterVisitRequest>()
            val exception = IOException("Network issue")
            coEvery { UserSessionManager.getUserInfo(datasource) } returns createFakeUserInfo(
                testSellerId
            )
            coEvery { visitApiService.registerVisit(capture(registerVisitRequestSlot)) } throws exception

            val result = repository.registerVisit(testCustomerId, testDateString, testObservations)
            advanceUntilIdle()

            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            coVerify(exactly = 1) { UserSessionManager.getUserInfo(datasource) }
            coVerify(exactly = 1) { visitApiService.registerVisit(registerVisitRequestSlot.captured) }
        }
}

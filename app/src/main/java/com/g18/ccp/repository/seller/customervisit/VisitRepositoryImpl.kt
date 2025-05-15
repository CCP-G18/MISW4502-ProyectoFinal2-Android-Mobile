package com.g18.ccp.repository.seller.customervisit

import android.util.Log
import com.g18.ccp.core.session.UserSessionManager
import com.g18.ccp.data.local.Datasource
import com.g18.ccp.data.remote.model.seller.visits.VisitData
import com.g18.ccp.data.remote.model.seller.visits.registervisit.RegisterVisitRequest
import com.g18.ccp.data.remote.model.seller.visits.registervisit.RegisterVisitResponse
import com.g18.ccp.data.remote.service.seller.visits.VisitService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VisitRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val datasource: Datasource,
    private val visitApiService: VisitService,
) : VisitRepository {

    override suspend fun getVisitsForCustomer(customerId: String): Result<List<VisitData>> {
        return withContext(dispatcher) {
            try {
                val sellerId = UserSessionManager.getUserInfo(datasource)?.id
                Log.d(
                    "VisitRepository",
                    "Fetching visits for customer: $customerId, seller: $sellerId"
                )
                sellerId?.let {
                    val response = visitApiService.getVisits(customerId)

                    if (response.isSuccessful && response.body() != null) {
                        val visitDataList = response.body()!!.data
                        Log.i(
                            "VisitRepository",
                            "Successfully fetched ${visitDataList.size} visits."
                        )
                        Result.success(visitDataList)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                        Log.e(
                            "VisitRepository",
                            "Failed to fetch visits: ${response.code()} - $errorBody"
                        )
                        Result.failure(Exception("Error ${response.code()}: $errorBody"))
                    }
                } ?: run {
                    Log.e("VisitRepository", "Seller ID is null")
                    Result.failure(Exception("Seller ID is null"))
                }
            } catch (e: Exception) {
                Log.e("VisitRepository", "Exception fetching visits", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun registerVisit(
        customerId: String,
        date: String,
        observations: String
    ): Result<RegisterVisitResponse> = withContext(dispatcher) {
        try {
            val sellerId = UserSessionManager.getUserInfo(datasource)?.id.orEmpty()
            Log.d(
                "VisitRepository",
                "Registering visit for customer: $customerId, seller: $sellerId, date: $date"
            )
            val requestBody = RegisterVisitRequest(
                customerId = customerId,
                sellerId = sellerId,
                date = date,
                observations = observations
            )
            val response = visitApiService.registerVisit(requestBody)

            if (response.isSuccessful && response.body() != null) {
                Log.i(
                    "VisitRepository",
                    "Visit registered successfully: ${response.body()?.message}"
                )
                Result.success(response.body()!!)
            } else {
                val errorBody =
                    response.errorBody()?.string() ?: "Error desconocido al registrar visita"
                Log.e(
                    "VisitRepository",
                    "Failed to register visit: ${response.code()} - $errorBody"
                )
                Result.failure(Exception("Error ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("VisitRepository", "Exception registering visit", e)
            Result.failure(e)
        }
    }
}

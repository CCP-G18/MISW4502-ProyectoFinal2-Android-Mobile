package com.g18.ccp.data.remote.service.seller.visits

import com.g18.ccp.data.remote.model.seller.visits.VisitListResponse
import com.g18.ccp.data.remote.model.seller.visits.registervisit.RegisterVisitRequest
import com.g18.ccp.data.remote.model.seller.visits.registervisit.RegisterVisitResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface VisitService {
    @GET("visits/customer/{customerId}")
    suspend fun getVisits(
        @Path("customerId") customerId: String
    ): Response<VisitListResponse>

    @POST("visits")
    suspend fun registerVisit(
        @Body visitRequest: RegisterVisitRequest
    ): Response<RegisterVisitResponse>
}

package com.g18.ccp.repository.seller.customervisit

import com.g18.ccp.data.remote.model.seller.visits.VisitData
import com.g18.ccp.data.remote.model.seller.visits.registervisit.RegisterVisitResponse

interface VisitRepository {
    suspend fun getVisitsForCustomer(customerId: String): Result<List<VisitData>>
    suspend fun registerVisit(
        customerId: String,
        date: String,
        observations: String
    ): Result<RegisterVisitResponse>
}

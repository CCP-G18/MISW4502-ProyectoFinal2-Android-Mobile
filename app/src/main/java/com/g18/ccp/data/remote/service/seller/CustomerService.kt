package com.g18.ccp.data.remote.service.seller

import com.g18.ccp.data.remote.model.seller.CustomerListResponse
import retrofit2.http.GET

interface CustomerService {
    @GET("customers")
    suspend fun getCustomers(): CustomerListResponse
}

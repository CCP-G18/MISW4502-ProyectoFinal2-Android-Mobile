package com.g18.ccp.repository.seller

import com.g18.ccp.data.remote.model.seller.CustomerListResponse

interface CustomerRepository {
    suspend fun getCustomers(): CustomerListResponse
}

package com.g18.ccp.repository.seller

import com.g18.ccp.data.remote.model.seller.CustomerListResponse
import com.g18.ccp.data.remote.service.seller.CustomerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CustomerRepositoryImpl(
    private val customerService: CustomerService
) : CustomerRepository {

    override suspend fun getCustomers(): CustomerListResponse {
        return withContext(Dispatchers.IO) {
            customerService.getCustomers()
        }
    }
}

package com.g18.ccp.repository.seller

import android.util.Log
import com.g18.ccp.core.utils.mapper.toCustomerEntityList
import com.g18.ccp.core.utils.mapper.toDomainCustomerModel
import com.g18.ccp.core.utils.mapper.toDomainCustomerModelList
import com.g18.ccp.data.local.model.room.dao.CustomerDao
import com.g18.ccp.data.remote.model.seller.CustomerData
import com.g18.ccp.data.remote.service.seller.CustomerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CustomerRepositoryImpl(
    private val customerService: CustomerService,
    private val customerDao: CustomerDao
) : CustomerRepository {

    override suspend fun getCustomers(): Flow<List<CustomerData>> {
        Log.d("CustomerRepositoryImpl", "getCustomers called - Reading from Room Flow")
        return customerDao.getAllCustomers()
            .map { entities ->
                Log.d(
                    "CustomerRepositoryImpl",
                    "Room emitted ${entities.size} customers. Mapping to Domain."
                )
                entities.toDomainCustomerModelList()
            }
    }

    override fun getCustomerById(customerId: String): Flow<CustomerData?> {
        Log.d(
            "CustomerRepositoryImpl",
            "getCustomerById called for ID: $customerId - Reading from Room Flow"
        )
        return customerDao.getCustomerById(customerId).map { entity ->
            Log.d(
                "CustomerRepositoryImpl",
                "Room emitted customer for ID $customerId. Exists: ${entity != null}. Mapping to Domain."
            )
            entity?.toDomainCustomerModel()
        }
    }

    override suspend fun refreshCustomers(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                Log.i("CustomerRepositoryImpl", "Attempting network refresh...")
                val response = customerService.getCustomers()

                if (response.status.equals("success", ignoreCase = true)) {
                    val entitiesToSave = response.data.toCustomerEntityList()
                    Log.d(
                        "CustomerRepositoryImpl",
                        "Network success. Mapping ${entitiesToSave.size} entities for Room."
                    )

                    customerDao.insertAll(entitiesToSave)
                    Result.success(Unit)
                } else {
                    Log.w(
                        "CustomerRepositoryImpl",
                        "API Error - Status: ${response.status}, Message: ${response.message}, Code: ${response.code}"
                    )
                    Result.failure(Exception("API Error ${response.code}: ${response.message}"))
                }
            } catch (e: Exception) {
                Log.e("CustomerRepositoryImpl", "Network/Refresh Error", e)
                Result.failure(e) // Informa fallo
            }
        }
    }
}

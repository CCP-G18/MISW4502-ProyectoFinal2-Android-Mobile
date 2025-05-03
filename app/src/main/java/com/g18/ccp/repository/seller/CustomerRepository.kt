package com.g18.ccp.repository.seller

import com.g18.ccp.data.remote.model.seller.CustomerData
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {

    /**
     * Obtiene la lista de clientes (desde Room).
     * Devuelve un Flow para que la UI reaccione a los cambios en la DB.
     */
    suspend fun getCustomers(): Flow<List<CustomerData>>

    /**
     * Obtiene un cliente específico por ID (desde Room).
     * Devuelve un Flow nullable para observar cambios o si no existe.
     */
    fun getCustomerById(customerId: String): Flow<CustomerData?>

    /**
     * Dispara una actualización de los datos desde el backend (BE),
     * guarda los resultados en Room y devuelve si la operación fue exitosa.
     */
    suspend fun refreshCustomers(): Result<Unit>
}

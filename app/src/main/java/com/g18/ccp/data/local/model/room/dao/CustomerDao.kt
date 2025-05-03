package com.g18.ccp.data.local.model.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.g18.ccp.data.local.model.room.model.CustomerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {

    /**
     * Inserta una lista de clientes. Si un cliente con la misma 'id' ya existe,
     * será reemplazado gracias a OnConflictStrategy.REPLACE.
     * Se marca como 'suspend' porque la inserción puede tomar tiempo.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customers: List<CustomerEntity>)

    /**
     * Obtiene todos los clientes ordenados por nombre.
     * Devuelve un Flow, lo que significa que Room notificará automáticamente
     * a los observadores cada vez que los datos en la tabla 'customers' cambien.
     */
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    /**
     * Obtiene un cliente específico por su ID.
     * Devuelve un Flow<CustomerEntity?> (nullable por si el ID no existe).
     * También se actualizará automáticamente si ese cliente específico cambia.
     */
    @Query("SELECT * FROM customers WHERE id = :customerId LIMIT 1")
    fun getCustomerById(customerId: String): Flow<CustomerEntity?>

    /**
     * Borra todos los clientes de la tabla. Útil antes de insertar una lista
     * completamente nueva si no quieres usar REPLACE.
     * Se marca como 'suspend'.
     */
    @Query("DELETE FROM customers")
    suspend fun deleteAllCustomers()
}

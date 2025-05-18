package com.g18.ccp.data.local.model.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.g18.ccp.data.local.model.room.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    /**
     * Obtiene todos las categorias ordenadas por nombre.
     * Devuelve un Flow, lo que significa que Room notificará automáticamente
     * a los observadores cada vez que los datos en la tabla 'categories' cambien.
     */
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    /**
     * Obtiene una categoría específica por su ID.
     * Devuelve un Flow<CategoryEntity?> (nullable por si el ID no existe).
     * También se actualizará automáticamente si ese cliente específico cambia.
     */
    @Query("SELECT * FROM categories WHERE id = :categoryId LIMIT 1")
    fun getCategoryById(categoryId: String): Flow<CategoryEntity?>

    /**
     * Borra todos los clientes de la tabla. Útil antes de insertar una lista
     * completamente nueva si no quieres usar REPLACE.
     * Se marca como 'suspend'.
     */
    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()
}

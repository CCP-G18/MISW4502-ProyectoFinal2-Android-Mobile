package com.g18.ccp.data.local.model.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.g18.ccp.data.local.model.room.converter.Converters
import com.g18.ccp.data.local.model.room.dao.CategoryDao
import com.g18.ccp.data.local.model.room.dao.CustomerDao
import com.g18.ccp.data.local.model.room.model.CategoryEntity
import com.g18.ccp.data.local.model.room.model.CustomerEntity

@Database(
    entities = [CustomerEntity::class, CategoryEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ccp_app_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

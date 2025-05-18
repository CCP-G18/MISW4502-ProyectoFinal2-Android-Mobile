package com.g18.ccp.data.local.model.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val createdAt: String,
    val name: String,
    val updatedAt: String,
)

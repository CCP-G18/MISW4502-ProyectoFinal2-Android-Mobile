package com.g18.ccp.data.local.model.room.converter

import androidx.room.TypeConverter
import com.g18.ccp.core.constants.enums.IdentificationType

class Converters {
    @TypeConverter
    fun fromIdentificationType(value: IdentificationType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toIdentificationType(value: String?): IdentificationType? {
        return value?.let { enumName ->
            try {
                IdentificationType.valueOf(enumName)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}

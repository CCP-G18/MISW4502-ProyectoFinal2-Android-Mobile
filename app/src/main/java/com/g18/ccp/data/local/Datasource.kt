package com.g18.ccp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class Datasource(private val context: Context) {
    private val Context.dataStore by preferencesDataStore("settings")

    suspend fun putString(key: String, value: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    suspend fun getString(key: String): String? {
        val preferences = context.dataStore.data.first()
        return preferences[stringPreferencesKey(key)]
    }
}

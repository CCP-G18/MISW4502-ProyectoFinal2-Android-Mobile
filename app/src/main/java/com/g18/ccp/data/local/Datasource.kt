package com.g18.ccp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

class Datasource(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun putString(key: String, value: String) {
        dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    suspend fun getString(key: String): String? {
        val preferences = dataStore.data.first()
        return preferences[stringPreferencesKey(key)]
    }
}

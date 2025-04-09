package com.g18.ccp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class DatasourceTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var datasource: Datasource
    private val key = "test_key"
    private val value = "test_value"
    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var fakePrefsFlow: MutableStateFlow<Preferences>

    @Before
    fun setUp() {
        dataStore = PreferenceDataStoreFactory.create {
            File(tempFolder.root, "test.preferences_pb")
        }
        datasource = Datasource(dataStore)

        val preferences: Preferences = mockk()
        fakePrefsFlow = MutableStateFlow(preferences)
    }

    @Test
    fun `given value when putString then value is saved`() = runTest {
        datasource.putString(key, value)
        val result = dataStore.data.first()[stringPreferencesKey(key)]
        assertEquals(value, result)
    }

    @Test
    fun `given stored value when getString then value is returned`() = runTest {
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey(key)] = value
        }

        val result = datasource.getString(key)
        assertEquals(value, result)
    }
}

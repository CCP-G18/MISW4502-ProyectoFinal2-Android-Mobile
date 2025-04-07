package com.g18.ccp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.g18.ccp.data.local.Datasource
import org.koin.dsl.module

val coreModule = module {
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create {
            get<Context>().dataStoreFile("settings.preferences_pb")
        }
    }
    single {
        Datasource(dataStore = get())
    }
}

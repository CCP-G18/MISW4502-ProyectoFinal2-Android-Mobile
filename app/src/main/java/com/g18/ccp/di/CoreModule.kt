package com.g18.ccp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.g18.ccp.data.local.Datasource
import com.g18.ccp.data.local.model.room.database.AppDatabase
import com.g18.ccp.repository.user.UserRepository
import com.g18.ccp.repository.user.UserRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single { AppDatabase.getDatabase(androidContext()) }

    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create {
            get<Context>().dataStoreFile("settings.preferences_pb")
        }
    }
    single {
        Datasource(dataStore = get())
    }
    single<UserRepository> {
        UserRepositoryImpl(datasource = get())
    }
}

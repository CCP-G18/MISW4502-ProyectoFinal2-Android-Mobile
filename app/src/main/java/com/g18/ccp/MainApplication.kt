package com.g18.ccp

import android.app.Application
import com.g18.ccp.di.authModule
import com.g18.ccp.di.coreModule
import com.g18.ccp.di.networkModule
import com.g18.ccp.di.orderModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(networkModule)
            modules(authModule)
            modules(coreModule)
            modules(orderModule)
        }
    }
}

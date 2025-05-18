package com.g18.ccp.di

import com.g18.ccp.core.utils.auth.AuthInterceptor
import com.g18.ccp.core.utils.auth.AuthenticationManager
import com.g18.ccp.core.utils.network.RetrofitProvider
import com.g18.ccp.data.remote.service.auth.AuthService
import com.g18.ccp.data.remote.service.auth.register.client.RegisterClientService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val networkModule = module {

    single { AuthenticationManager(androidContext()) }

    single { AuthInterceptor(get()) }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(loggingInterceptor)
//            .addInterceptor(MockInterceptor())
            .build()
    }

    single {
        RetrofitProvider(get())
    }

    single<AuthService> { get<RetrofitProvider>().instance.create(AuthService::class.java) }
    single<RegisterClientService> { get<RetrofitProvider>().instance.create(RegisterClientService::class.java) }
}

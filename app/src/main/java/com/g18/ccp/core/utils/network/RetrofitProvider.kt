package com.g18.ccp.core.utils.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitProvider(
    private val okHttpClient: OkHttpClient
) {
    val instance = Retrofit.Builder()
        .baseUrl("https://proxy-service-947858281009.us-central1.run.app")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

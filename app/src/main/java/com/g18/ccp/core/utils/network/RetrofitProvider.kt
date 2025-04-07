package com.g18.ccp.core.utils.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitProvider(
    private val okHttpClient: OkHttpClient
) {
    val instance = Retrofit.Builder()
        .baseUrl("http://34.160.33.188/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

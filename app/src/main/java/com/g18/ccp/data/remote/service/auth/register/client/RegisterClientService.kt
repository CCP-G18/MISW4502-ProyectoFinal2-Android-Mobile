package com.g18.ccp.data.remote.service.auth.register.client

import com.g18.ccp.data.remote.model.auth.register.client.RegisterClientRequest
import com.g18.ccp.data.remote.model.auth.register.client.RegisterClientResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterClientService {
    @POST("customers")
    suspend fun registerClient(
        @Body request: RegisterClientRequest
    ): RegisterClientResponse
}

package com.g18.ccp.repository.auth.register.client

import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.data.remote.model.auth.register.client.RegisterClientResponse

interface ClientRegisterRepository {
    suspend fun registerClient(
        identificationType: String,
        identificationNumber: Long,
        country: String,
        city: String,
        name: String,
        lastname: String,
        email: String,
        password: String,
        address: String
    ): Output<RegisterClientResponse>
}

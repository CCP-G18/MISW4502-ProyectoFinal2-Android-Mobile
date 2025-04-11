package com.g18.ccp.repository.auth.register.client

import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.data.remote.model.auth.register.client.RegisterClientRequest
import com.g18.ccp.data.remote.model.auth.register.client.RegisterClientResponse
import com.g18.ccp.data.remote.model.auth.register.client.RegisterUser
import com.g18.ccp.data.remote.service.auth.register.client.RegisterClientService

class ClientRegisterRepositoryImpl(
    private val api: RegisterClientService
) : ClientRegisterRepository {
    override suspend fun registerClient(
        identificationType: String,
        identificationNumber: Long,
        country: String,
        city: String,
        name: String,
        lastname: String,
        email: String,
        password: String,
        address: String
    ): Output<RegisterClientResponse> = try {
        val request = RegisterClientRequest(
            identificationType = identificationType,
            identificationNumber = identificationNumber,
            country = country,
            city = city,
            address = address,
            user = RegisterUser(
                name = name,
                lastname = lastname,
                email = email,
                password = password
            )
        )

        val response = api.registerClient(request)

        Output.Success(response)
    } catch (e: Exception) {
        Output.Failure(e, e.message)
    }
}

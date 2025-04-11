package com.g18.ccp.repository.auth.register.client

import com.g18.ccp.core.utils.network.Output
import com.g18.ccp.data.remote.model.auth.register.client.ClientData
import com.g18.ccp.data.remote.model.auth.register.client.RegisterClientRequest
import com.g18.ccp.data.remote.model.auth.register.client.RegisterClientResponse
import com.g18.ccp.data.remote.model.auth.register.client.RegisterUser
import com.g18.ccp.data.remote.model.auth.register.client.RegisteredUser
import com.g18.ccp.data.remote.service.auth.register.client.RegisterClientService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ClientRegisterRepositoryImplTest {

    private lateinit var repository: ClientRegisterRepositoryImpl
    private lateinit var service: RegisterClientService

    private val mockRequest = RegisterClientRequest(
        identificationType = "CC",
        identificationNumber = 123456789,
        country = "Colombia",
        city = "Bogotá",
        address = "Calle 123",
        user = RegisterUser(
            name = "Juan",
            lastname = "Pérez",
            email = "juan@example.com",
            password = "123456"
        )
    )

    private val mockResponse = RegisterClientResponse(
        code = 201,
        data = ClientData(
            identificationType = "CC",
            identificationNumber = 123456789,
            country = "Colombia",
            city = "Bogotá",
            address = "Calle 123",
            user = RegisteredUser(
                name = "Juan",
                lastname = "Pérez",
                email = "juan@example.com",
                role = "admin"
            )
        ),
        message = "Cliente creado con éxito",
        status = "success"
    )

    @Before
    fun setUp() {
        service = mockk()
        repository = ClientRegisterRepositoryImpl(service)
    }

    @Test
    fun `given valid data when registerClient then returns success`() = runTest {
        // Given
        coEvery { service.registerClient(any()) } returns mockResponse

        // When
        val result = repository.registerClient(
            identificationType = mockRequest.identificationType,
            identificationNumber = mockRequest.identificationNumber,
            country = mockRequest.country,
            city = mockRequest.city,
            name = mockRequest.user.name,
            lastname = mockRequest.user.lastname,
            email = mockRequest.user.email,
            password = mockRequest.user.password,
            address = mockRequest.address
        )

        // Then
        assert(result is Output.Success)
        assertEquals(mockResponse, (result as Output.Success).data)
        coVerify(exactly = 1) { service.registerClient(any()) }
    }

    @Test
    fun `given exception when registerClient then returns failure`() = runTest {
        // Given
        val exception = RuntimeException("Something went wrong")
        coEvery { service.registerClient(any()) } throws exception

        // When
        val result = repository.registerClient(
            identificationType = mockRequest.identificationType,
            identificationNumber = mockRequest.identificationNumber,
            country = mockRequest.country,
            city = mockRequest.city,
            name = mockRequest.user.name,
            lastname = mockRequest.user.lastname,
            email = mockRequest.user.email,
            password = mockRequest.user.password,
            address = mockRequest.address
        )

        // Then
        assert(result is Output.Failure<*>)
        assertEquals(exception, (result as Output.Failure<*>).exception)
        assertEquals("Something went wrong", result.message)
        coVerify(exactly = 1) { service.registerClient(any()) }
    }
}

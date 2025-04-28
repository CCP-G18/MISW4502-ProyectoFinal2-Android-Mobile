package com.g18.ccp.core.utils.network.mock

import com.g18.ccp.core.constants.enums.IdentificationType
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val method = request.method

        val responseJson = when {
            method == "GET" && path == "/customers" -> """
                {
                    "code": 200,
                    "data": [
                    {
                                "address": "Calle ",
                                "city": "Medellín",
                                "country": "Colombia",
                                "email": "ana.villanueva@example.com",
                                "id": "ed82b70f-f2e4-4709-b4af-9105a07187c3",
                                "identification_number": 123456789,
                                "identification_type": "CC",
                                "name": "Ana Villanueva"
                            },
                            {
                                "address": "Calle ",
                                "city": "Medellín",
                                "country": "Colombia",
                                "email": "gonzalo.arias@example.com",
                                "id": "bc9a4bdd-c768-4f24-bc02-0892ec148d83",
                                "identification_number": 123456789,
                                "identification_type": "CC",
                                "name": "Gonzalo Arias"
                            },
                        {
                            "id": "cust_001",
                            "name": "Gonzalo Hurtado Muñoz",
                            "address": "Calle 15 # 20-30",
                            "city": "Bogotá",
                            "country": "Colombia",
                            "email": "gonzalo.h@example.com",
                            "identification_number": "12345678",
                            "identificationType": "${IdentificationType.CC.name}" 
                        },
                        {
                            "id": "cust_007",
                            "name": "Brayan García",
                            "address": "Calle 75 # 20-30",
                            "city": "Bogotá",
                            "country": "Colombia",
                            "email": "brayan.h@hotmail.com",
                            "identification_number": "135354",
                            "identificationType": "${IdentificationType.CC.name}" 
                        },
                        {
                            "id": "cust_002",
                            "name": "Andrés Ortiz Calle",
                            "address": "Carrera 7 # 82-01",
                            "city": "Bogotá",
                            "country": "Colombia",
                            "email": "andres.o@example.com",
                            "identification_number": "87654321",
                            "identificationType": "${IdentificationType.CC.name}"
                        },
                        {
                            "id": "cust_003",
                            "name": "Camilo Hernández",
                            "address": "Avenida Chile # 70-50",
                            "city": "Bogotá",
                            "country": "Colombia",
                            "email": "camilo.h@example.com",
                            "identification_number": "11223344",
                            "identificationType": "${IdentificationType.CE.name}"
                        },
                        {
                             "id": "cust_004",
                             "name": "Extranjero Pasaporte",
                             "address": "Calle Falsa 123",
                             "city": "Bogotá",
                             "country": "Colombia",
                             "email": "pasaporte@example.com",
                             "identification_number": "PA98765",
                             "identificationType": "${IdentificationType.PASSPORT.name}"
                        }
                    ],
                    "message": "Clientes obtenidos desde MockInterceptor",
                    "status": "success"
                }
            """.trimIndent()

            method == "GET" && path == "/products" -> """
                {
                    "code": 201,
                    "data": [ 
                        {
                            "id": "1",
                            "name": "Papas",
                            "quantity": 1,
                            "unit_amount": 5000.0,
                            "image_url": "https://mercasur.com.co/rails/active_storage/representations/proxy/eyJf..."
                        },
                        {
                            "id": "2",
                            "quantity": 2,
                            "name": "Arroz Diana",
                            "unit_amount": 3500.0,
                            "image_url": "https://mercayahorra.com/wp-content/uploads/2020/06/arroz_diana_500gr.jpg"
                        },
                        {
                            "id": "3",
                            "name": "Huevos AA",
                            "quantity": 2,
                            "unit_amount": 12000.0,
                            "image_url": "https://mercarapidaws.nyc3.cdn.digitaloceanspaces.com/products/206/1584230930-Huevos%20Santa%20Reina%20AA%2030und.jpg"
                        },
                        {
                            "id": "4",
                            "name": "Huevos AAA",
                            "quantity": 2,
                            "unit_amount": 48000.0,
                            "image_url": "https://mercarapidaws.nyc3.cdn.digitaloceanspaces.com/products/206/1584230930-Huevos%20Santa%20Reina%20AA%2030und.jpg"
                        }
                    ]
                }
            """.trimIndent()

            method == "POST" && path == "/order" -> """
                {
                    "date": "2025-05-27",
                    "total": 250000.0,
                    "items": [
                        {
                            "product_id": "3fdecf5d-269c-4de7-acaa-e5e5f064ed30",
                            "quantity": 2,
                            "price": 5000.0
                        },
                        {
                            "product_id": "04f9e209-d54e-4329-923c-63f10624b226",
                            "quantity": 3,
                            "price": 8500.0
                        }
                    ]
                }
            """.trimIndent()

            method == "POST" && path == "/login" -> """
                {
    "code": 200,
    "data": {
        "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmcmVzaCI6ZmFsc2UsImlhdCI6MTc0NTQ2MTgzMywianRpIjoiNTMzNDY3ZGEtNTI1Zi00ZmFjLTgzNmUtOGNiZGRlNzRiMGJiIiwidHlwZSI6ImFjY2VzcyIsInN1YiI6IjAxNGM5ZTNkLTU3ZmYtNGQzYy1hZGYzLWI2ODU3OTkyMjc4NSIsIm5iZiI6MTc0NTQ2MTgzMywiY3NyZiI6IjM5NWJmYjc3LTc2MmItNDc5YS1hYWRjLTAyMDMyNTJjYWM5ZiIsImV4cCI6MTc0NTQ2NTQzMywicm9sZSI6ImFkbWluIn0.YNQT1bDejTKpUx0nPHo5UHah28CxpQn_6HQWgDuDZCo",
        "user": {
            "email": "fernando.p@example.com",
            "id": "014c9e3d-57ff-4d3c-adf3-b68579922785",
            "role": "seller",
            "username": "fernando.p"
        }
    },
    "message": "Usuario logueado con éxito",
    "status": "success"
}
            """.trimIndent()

            else -> return chain.proceed(request) // Para cualquier otra ruta, se hace la petición real
        }

        return chain.proceed(request).newBuilder()
            .code(200)
            .protocol(Protocol.HTTP_1_1)
            .message("Mocked response")
            .body(responseJson.toResponseBody("application/json".toMediaType()))
            .build()
    }
}

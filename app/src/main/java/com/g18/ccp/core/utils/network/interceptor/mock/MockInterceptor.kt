package com.g18.ccp.core.utils.network.interceptor.mock

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

            method == "GET" && path.endsWith("/visits") -> { // Usamos endsWith para más flexibilidad
                // Opcionalmente, podrías leer los query params customerId/sellerId aquí
                // val customerIdQuery = request.url.queryParameter("customerId")
                // Log.d("MockInterceptor", "/visits called for customerId: $customerIdQuery")
                """
                {"code":200,"data":[{"created_at":"2025-05-14T16:12:02.729418","customer_id":"3391bc52-2cda-45ff-9076-b6585d595198","id":"307dfe2b-9852-4188-9f9d-695de307f2c3","observations":"Todo bien","register_date":"2025-05-14","seller_id":"5dcf93da-f392-4d85-ad5c-fcf202a4eb87","updated_at":"2025-05-14T16:12:02.729418"}],"message":"Visitas encontradas con \u00e9xito","status":"success"}
                """.trimIndent()
            }

            method == "POST" && path.endsWith("/visit") -> {
                // Opcional: podrías leer el body de la request aquí para depurar
                // val requestBody = chain.request().bodyToString()
                // Log.d("MockInterceptor", "POST /visit with body: $requestBody")
                """
                {"code":201,"data":{"created_at":"2025-05-14T16:12:02.729418","customer_id":"3391bc52-2cda-45ff-9076-b6585d595198","id":"307dfe2b-9852-4188-9f9d-695de307f2c3","observations":"Todo bien","register_date":"2025-05-14","seller_id":"5dcf93da-f392-4d85-ad5c-fcf202a4eb87","updated_at":"2025-05-14T16:12:02.729418"},"message":"Visita creada con \u00e9xito","status":"success"}
                """.trimIndent()
            }

            else -> return chain.proceed(request)
        }

        return chain.proceed(request).newBuilder()
            .code(200)
            .protocol(Protocol.HTTP_1_1)
            .message("Mocked response")
            .body(responseJson.toResponseBody("application/json".toMediaType()))
            .build()
    }
}

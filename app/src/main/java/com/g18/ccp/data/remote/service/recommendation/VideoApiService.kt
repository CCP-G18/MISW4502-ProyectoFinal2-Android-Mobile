package com.g18.ccp.data.remote.service.recommendation

import com.g18.ccp.data.remote.model.recommendation.RecommendationListResponse
import com.g18.ccp.data.remote.model.recommendation.VideoUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface VideoApiService {
    @Multipart
    @POST("recommendations")
    suspend fun uploadVideoRecommendation(
        @Part video: MultipartBody.Part,
        @Part("customer_id") customerId: RequestBody,
        @Part("seller_id") sellerId: RequestBody,
    ): Response<VideoUploadResponse>

    @PUT("recommendations/{recommendationId}")
    suspend fun generateRecommendationWithIA(
        @Path("recommendationId") recmmendationId: String
    ): Response<VideoUploadResponse>

    @GET("recommendations/sellers/{sellerId}/customers/{customerId}")
    suspend fun getRecommendations(
        @Path("customerId") customerId: String,
        @Path("sellerId") sellerId: String,
    ): Response<RecommendationListResponse>
}

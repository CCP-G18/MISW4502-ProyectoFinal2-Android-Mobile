package com.g18.ccp.data.remote.service.recommendation

import com.g18.ccp.data.remote.model.recommendation.VideoUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface VideoApiService {
    @Multipart
    @POST("recommendations")
    suspend fun uploadVideoRecommendation(
        @Part("video") videoFile: MultipartBody.Part,
        @Part("customer_id") customerId: RequestBody,
        @Part("seller_id") sellerId: RequestBody,
    ): Response<VideoUploadResponse>
}

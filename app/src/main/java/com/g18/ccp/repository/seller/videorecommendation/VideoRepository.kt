package com.g18.ccp.repository.seller.videorecommendation

import android.net.Uri
import com.g18.ccp.data.remote.model.recommendation.VideoUploadResponse

interface VideoRepository {
    suspend fun deleteVideo(videoUri: Uri): Result<Unit>
    suspend fun saveVideo(sourceUri: Uri, desiredName: String): Result<Uri>
    suspend fun uploadVideo(
        videoFileUri: Uri,
        videoFileName: String,
        customerId: String,
    ): Result<VideoUploadResponse>
}

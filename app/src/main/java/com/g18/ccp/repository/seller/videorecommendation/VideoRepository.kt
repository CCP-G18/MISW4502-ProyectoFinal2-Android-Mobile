package com.g18.ccp.repository.seller.videorecommendation

import android.net.Uri

interface VideoRepository {
    suspend fun deleteVideo(videoUri: Uri): Result<Unit>
    suspend fun saveVideo(sourceUri: Uri, desiredName: String): Result<Uri>
}

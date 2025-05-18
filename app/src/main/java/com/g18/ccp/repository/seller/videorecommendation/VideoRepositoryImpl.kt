package com.g18.ccp.repository.seller.videorecommendation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.g18.ccp.core.session.UserSessionManager
import com.g18.ccp.data.local.Datasource
import com.g18.ccp.data.remote.model.recommendation.RecommendationData
import com.g18.ccp.data.remote.model.recommendation.RecommendationListResponse
import com.g18.ccp.data.remote.model.recommendation.VideoUploadResponse
import com.g18.ccp.data.remote.service.recommendation.VideoApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class VideoRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val applicationContext: Context,
    private val datasource: Datasource,
    private val videoApiService: VideoApiService
) : VideoRepository {

    override suspend fun deleteVideo(videoUri: Uri): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("VideoRepositoryImpl", "Attempting to delete video URI: $videoUri")
                val deletedRows = applicationContext.contentResolver.delete(videoUri, null, null)
                if (deletedRows > 0) {
                    Log.i("VideoRepositoryImpl", "Successfully deleted video: $videoUri")
                    Result.success(Unit)
                } else {
                    Log.w(
                        "VideoRepositoryImpl",
                        "No rows deleted for video URI (file might not exist): $videoUri"
                    )
                    Result.success(Unit)
                }
            } catch (e: Exception) {
                Log.e("VideoRepositoryImpl", "Error deleting video URI: $videoUri", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun saveVideo(sourceUri: Uri, desiredName: String): Result<Uri> {
        return withContext(Dispatchers.IO) {
            val destinationFolder = File(applicationContext.filesDir, "customer_videos")
            if (!destinationFolder.exists()) {
                if (!destinationFolder.mkdirs()) {
                    Log.e(
                        "VideoRepositoryImpl",
                        "Failed to create destination folder: ${destinationFolder.absolutePath}"
                    )
                    return@withContext Result.failure(Exception("Cannot create video directory"))
                }
            }

            val destinationFile = File(destinationFolder, desiredName)
            Log.d(
                "VideoRepositoryImpl",
                "Attempting to save video from $sourceUri to ${destinationFile.absolutePath}"
            )

            try {
                applicationContext.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                    FileOutputStream(destinationFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                    ?: return@withContext Result.failure(Exception("Failed to open input stream for source URI"))

                val savedUri = FileProvider.getUriForFile(
                    applicationContext,
                    "${applicationContext.packageName}.provider",
                    destinationFile
                )
                Log.i(
                    "VideoRepositoryImpl",
                    "Successfully saved video to: ${destinationFile.absolutePath}. URI: $savedUri"
                )
                Result.success(savedUri)
            } catch (e: Exception) {
                Log.e("VideoRepositoryImpl", "Error saving video file", e)
                destinationFile.delete()
                Result.failure(e)
            }
        }
    }

    override suspend fun uploadVideo(
        videoFileUri: Uri,
        videoFileName: String,
        customerId: String
    ): Result<VideoUploadResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("VideoRepositoryImpl", "Preparing to upload video: $videoFileUri")
                val sellerId = UserSessionManager.getUserInfo(datasource)?.id.orEmpty()
                val customerIdRequestBody =
                    customerId.toRequestBody("text/plain".toMediaTypeOrNull())
                val sellerIdRequestBody = sellerId.toRequestBody("text/plain".toMediaTypeOrNull())

                val videoFilePart = applicationContext.contentResolver.openInputStream(videoFileUri)
                    ?.use { inputStream ->
                        val requestFile =
                            inputStream.readBytes().toRequestBody("video/mp4".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("video", videoFileName, requestFile)
                    }
                    ?: return@withContext Result.failure(IOException("Could not open input stream for video URI"))


                Log.d("VideoRepositoryImpl", "Calling uploadVideoRecommendation API...")
                val response = videoApiService.uploadVideoRecommendation(
                    video = videoFilePart,
                    customerId = customerIdRequestBody,
                    sellerId = sellerIdRequestBody
                )

                if (response.isSuccessful && response.body() != null) {
                    Log.i("VideoRepositoryImpl", "Video upload successful: ${response.body()}")
                    val responseGenerate = videoApiService.generateRecommendationWithIA(
                        recmmendationId = response.body()!!.data.id
                    )
                    if (responseGenerate.isSuccessful) {
                        Result.success(responseGenerate.body()!!)
                    } else {
                        val errorBody = responseGenerate.errorBody()?.string() ?: "Unknown error"
                        Log.e(
                            "VideoRepositoryImpl",
                            "Video upload failed: ${responseGenerate.code()} - $errorBody"
                        )
                        Result.failure(IOException("Upload failed: ${responseGenerate.code()} - $errorBody"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e(
                        "VideoRepositoryImpl",
                        "Video upload failed: ${response.code()} - $errorBody"
                    )
                    Result.failure(IOException("Upload failed: ${response.code()} - $errorBody"))
                }
            } catch (e: Exception) {
                Log.e("VideoRepositoryImpl", "Exception during video upload", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun getRecommendations(customerId: String): Result<List<RecommendationData>> {
        return withContext(dispatcher) {
            try {
                val sellerId = UserSessionManager.getUserInfo(datasource)?.id
                Log.d(
                    "RecommendationRepository",
                    "Fetching recommendations for customer: $customerId, seller: $sellerId"
                )

                sellerId?.let {
                    val response = videoApiService.getRecommendations(customerId, sellerId)

                    if (response.isSuccessful && response.body() != null) {
                        val recommendations = response.body()!!.data
                        Log.i(
                            "RecommendationRepository",
                            "Successfully fetched ${recommendations.size} recommendations."
                        )
                        Result.success(recommendations)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                        Log.e(
                            "RecommendationRepository",
                            "Failed to fetch recommendations: ${response.code()} - $errorBody"
                        )
                        Result.failure(Exception("Error ${response.code()}: $errorBody"))
                    }
                } ?: run {
                    Log.e("RecommendationRepository", "Seller ID is null")
                    Result.failure(Exception("Seller ID is null"))
                }
            } catch (e: Exception) {
                Log.e("RecommendationRepository", "Exception fetching recommendations", e)
                Result.failure(e)
            }
        }
    }
}

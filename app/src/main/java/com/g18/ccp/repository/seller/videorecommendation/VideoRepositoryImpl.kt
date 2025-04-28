package com.g18.ccp.repository.seller.videorecommendation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class VideoRepositoryImpl(
    private val applicationContext: Context
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
}

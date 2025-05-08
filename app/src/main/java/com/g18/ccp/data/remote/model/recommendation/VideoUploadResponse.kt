package com.g18.ccp.data.remote.model.recommendation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoUploadResponse(
    val code: Int,
    val data: VideoUploadData,
    val message: String,
    val status: String
) : Parcelable

@Parcelize
data class VideoUploadData(
    val createdAt: String,
    val customerId: String,
    val id: String,
    val sellerId: String,
    val updatedAt: String,
    val video: String
) : Parcelable

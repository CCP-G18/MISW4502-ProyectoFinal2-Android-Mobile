package com.g18.ccp.presentation.seller.recommendation

import android.net.Uri

sealed interface RecommendationsUiState {
    data class Idle(
        val defaultVideoName: String = "",
        val showDeleteConfirmDialog: Boolean = false,
        val message: String? = null
    ) : RecommendationsUiState

    data class Preview(
        val videoUri: Uri,
        val videoName: String,
        val showDeleteConfirmDialog: Boolean = false,
        val message: String? = null
    ) : RecommendationsUiState

    // data class Error(val message: String) : RecommendationsUiState
}

package com.g18.ccp.presentation.seller.recommendation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.core.constants.CUSTOMER_ID_ARG
import com.g18.ccp.core.session.UserSessionManager
import com.g18.ccp.data.local.Datasource
import com.g18.ccp.presentation.seller.customervisit.list.VisitsScreenUiState
import com.g18.ccp.repository.seller.videorecommendation.VideoRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class SellerCustomerRecommendationsViewModel(
    savedStateHandle: SavedStateHandle,
    private val videoRepository: VideoRepository,
    private val datasource: Datasource
) : ViewModel() {

    private val customerId: String = checkNotNull(savedStateHandle[CUSTOMER_ID_ARG])
    private var videoCounter = 1

    private val _uiState = MutableStateFlow<RecommendationsUiState>(RecommendationsUiState.Idle())
    val uiState: StateFlow<RecommendationsUiState> = _uiState.asStateFlow()

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = RecommendationsUiState.Loading

            val recommendationsResult = videoRepository.getRecommendations(customerId)
            recommendationsResult.onSuccess { recommendationDataList ->
                Log.d("VisitsVM", "Successfully fetched ${recommendationDataList.size} visits.")
                val displayRecommendationDeferred = recommendationDataList.map { recommendationData ->
                    async {
                        RecommendationDisplayItem(
                            id = recommendationData.id,
                            recommendations = recommendationData.recommendations,
                            recommendationDate = formatDate(recommendationData.recommendationDate),
                        )
                    }
                }
                val displayRecommendations =
                    displayRecommendationDeferred.awaitAll().sortedByDescending { it.recommendationDate }
                _uiState.value = RecommendationsUiState.Success(
                    recommendations = displayRecommendations
                )
            }
            recommendationsResult.onFailure { exception ->
                Log.e("VisitsVM", "Failed to fetch visits", exception)

                _uiState.value = RecommendationsUiState.Success(
                    recommendations = listOf()
                )
            }
        }
    }

    fun onVideoRecorded(tempUri: Uri?) {
        if (tempUri == null) {
            _uiState.value = RecommendationsUiState.Idle(message = "Grabación cancelada o fallida.")
            return
        }

        viewModelScope.launch {
            val sequentialVideoName = "video_${videoCounter}.mp4"
            Log.d(
                "ViewModel",
                "Attempting to save video $sequentialVideoName from temp URI: $tempUri"
            )

            val saveResult = videoRepository.saveVideo(tempUri, sequentialVideoName)

            saveResult.onSuccess { savedUri ->
                Log.i("ViewModel", "Video saved successfully by repo. New URI: $savedUri")
                _uiState.value = RecommendationsUiState.Preview(
                    videoUri = savedUri,
                    videoName = sequentialVideoName,
                    message = "Vídeo guardado exitosamente"
                )
                videoCounter++
            }
            saveResult.onFailure { exception ->
                Log.e("ViewModel", "Failed to save video via repository", exception)
                _uiState.value = RecommendationsUiState.Idle(
                    message = "Error al guardar el vídeo: ${exception.message}"
                )
            }
        }
    }

    private fun formatDate(date: java.util.Date): String {
        return try {
            val outputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            outputFormat.format(date)
        } catch (e: Exception) {
            Log.e("VisitDataMapping", "Error formatting date object: $date", e)
            date.toString()
        }
    }

    fun onConfirmDelete() {
        val currentState = _uiState.value
        if (currentState is RecommendationsUiState.Preview) {
            val uriToDelete = currentState.videoUri
            viewModelScope.launch {
                Log.d("ViewModel", "Calling repository to delete video: $uriToDelete")
                val deleteResult = videoRepository.deleteVideo(uriToDelete)

                _uiState.update {
                    val message = if (deleteResult.isSuccess) {
                        Log.i("ViewModel", "Video deletion successful via repository.")
                        "Vídeo eliminado exitosamente."
                    } else {
                        Log.e(
                            "ViewModel",
                            "Video deletion failed via repository",
                            deleteResult.exceptionOrNull()
                        )
                        "Error al eliminar el vídeo: ${deleteResult.exceptionOrNull()?.message}"
                    }
                    RecommendationsUiState.Idle(
                        showDeleteConfirmDialog = false,
                        message = message
                    )
                }
            }
        } else {
            _uiState.update {
                if (it is RecommendationsUiState.Idle) it.copy(showDeleteConfirmDialog = false) else it
            }
        }
    }


    fun onCancelPreviewClick() {
        val currentState = _uiState.value
        if (currentState is RecommendationsUiState.Preview) {
            val uriToDelete = currentState.videoUri
            viewModelScope.launch {
                Log.d(
                    "ViewModel",
                    "CancelPreview clicked. Attempting to delete video: $uriToDelete"
                )
                val deleteResult = videoRepository.deleteVideo(uriToDelete)
                if (deleteResult.isSuccess) {
                    Log.i("ViewModel", "Video deleted successfully on cancel.")
                    _uiState.value = RecommendationsUiState.Idle()
                } else {
                    Log.e(
                        "ViewModel",
                        "Failed to delete video on cancel",
                        deleteResult.exceptionOrNull()
                    )
                    _uiState.value = currentState.copy(
                        message = "Error al borrar el vídeo temporal: ${deleteResult.exceptionOrNull()?.message}"
                    )
                }
            }
        } else {
            _uiState.value = RecommendationsUiState.Idle()
        }
    }


    fun clearMessage() {
        _uiState.update { currentState ->
            when (currentState) {
                is RecommendationsUiState.Idle -> currentState.copy(message = null)
                is RecommendationsUiState.Preview -> currentState.copy(message = null)
                RecommendationsUiState.Loading -> TODO()
                is RecommendationsUiState.Success -> TODO()
            }
        }
    }


    fun simulateCameraError() {
        _uiState.value =
            RecommendationsUiState.Idle(message = "Error: Cámara no disponible o permiso denegado.")
    }


    fun onReceiveRecommendationClick() {
        val currentState = _uiState.value
        if (currentState is RecommendationsUiState.Preview) {
            Log.d("ViewModel", "Recibir Recomendación clickeado. Subiendo vídeo...")
            _uiState.value = currentState.copy(
                message = "Subiendo vídeo...",
                showDeleteConfirmDialog = false
            ) // Indica subida

            viewModelScope.launch {
                val uploadResult = videoRepository.uploadVideo(
                    videoFileUri = currentState.videoUri,
                    videoFileName = currentState.videoName,
                    customerId = customerId,
                )

                uploadResult.onSuccess { response ->
                    Log.i("ViewModel", "Subida exitosa: ${response}")
                    _uiState.value =
                        currentState.copy(message = "Recomendación solicitada: ${response}")
                }
                uploadResult.onFailure { exception ->
                    Log.e("ViewModel", "Fallo en la subida", exception)
                    _uiState.value =
                        currentState.copy(message = "Error al solicitar recomendación: ${exception.message}")
                }
            }
        }
    }

    fun onCancelDelete() {
        _uiState.update { currentState ->
            when (currentState) {
                is RecommendationsUiState.Idle -> currentState.copy(showDeleteConfirmDialog = false)
                is RecommendationsUiState.Preview -> currentState.copy(showDeleteConfirmDialog = false)
                RecommendationsUiState.Loading -> TODO()
                is RecommendationsUiState.Success -> TODO()
            }
        }
    }

    fun onDeleteClick() {
        _uiState.update { currentState ->
            when (currentState) {
                is RecommendationsUiState.Idle -> currentState.copy(showDeleteConfirmDialog = true)
                is RecommendationsUiState.Preview -> currentState.copy(showDeleteConfirmDialog = true)
                RecommendationsUiState.Loading -> TODO()
                is RecommendationsUiState.Success -> TODO()
            }
        }
    }
}


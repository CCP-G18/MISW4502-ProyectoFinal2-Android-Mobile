package com.g18.ccp.presentation.seller.recommendation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g18.ccp.core.constants.CUSTOMER_ID_ARG
import com.g18.ccp.repository.seller.videorecommendation.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SellerCustomerRecommendationsViewModel(
    savedStateHandle: SavedStateHandle,
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val customerId: String = checkNotNull(savedStateHandle[CUSTOMER_ID_ARG])
    private var videoCounter = 1

    private val _uiState = MutableStateFlow<RecommendationsUiState>(RecommendationsUiState.Idle())
    val uiState: StateFlow<RecommendationsUiState> = _uiState.asStateFlow()

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
            }
        }
    }


    fun simulateCameraError() {
        _uiState.value =
            RecommendationsUiState.Idle(message = "Error: Cámara no disponible o permiso denegado.")
    }


    fun onReceiveRecommendationClick() {
        Log.d("ViewModel", "Botón 'Recibir Recomendación' pulsado - Sin funcionalidad")
        _uiState.update { currentState ->
            when (currentState) {
                is RecommendationsUiState.Preview -> currentState
                    .copy(message = "Función 'Recibir Recomendación' no implementada.")

                else -> currentState
            }
        }
    }

    fun onCancelDelete() {
        _uiState.update { currentState ->
            when (currentState) {
                is RecommendationsUiState.Idle -> currentState.copy(showDeleteConfirmDialog = false)
                is RecommendationsUiState.Preview -> currentState.copy(showDeleteConfirmDialog = false)
            }
        }
    }

    fun onDeleteClick() {
        _uiState.update { currentState ->
            when (currentState) {
                is RecommendationsUiState.Idle -> currentState.copy(showDeleteConfirmDialog = true)
                is RecommendationsUiState.Preview -> currentState.copy(showDeleteConfirmDialog = true)
            }
        }
    }
}

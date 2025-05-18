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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class SellerCustomerRecommendationsViewModel(
    savedStateHandle: SavedStateHandle,
    private val videoRepository: VideoRepository,
) : ViewModel() {

    private val customerId: String = checkNotNull(savedStateHandle[CUSTOMER_ID_ARG])
    private var videoCounter = 1


    private val _uiState = MutableStateFlow<RecommendationsUiState>(RecommendationsUiState.Idle())
    val uiState: StateFlow<RecommendationsUiState> = _uiState.asStateFlow()


    fun loadInitialData() {
        Log.d(
            "SellerCustomerRecommendationsViewModel",
            "Ejecutando función loadInitialData"
        )
        viewModelScope.launch {
            _uiState.value = RecommendationsUiState.Loading

            val recommendationsResult = videoRepository.getRecommendations(customerId)
            recommendationsResult.onSuccess { recommendationDataList ->
                Log.d("SellerCustomerRecommendationsViewModel", "Successfully fetched ${recommendationDataList.size} recommendations.")
                val displayRecommendationDeferred = recommendationDataList.map { recommendationData ->
                    async {
                        RecommendationDisplayItem(
                            id = recommendationData.id,
                            recommendations = recommendationData.recommendations ?: "",
                            recommendationDate = recommendationData.recommendationDate ?: "",
                            createdAt = recommendationData.createdAt ?: "",
                        )
                    }
                }
                val displayRecommendations =
                    displayRecommendationDeferred.awaitAll().sortedByDescending { it.createdAt }
                _uiState.value = RecommendationsUiState.LoadRecommendations(
                    recommendations = displayRecommendations
                )
            }
            recommendationsResult.onFailure { exception ->
                Log.e("VisitsVM", "Failed to fetch visits", exception)

                _uiState.value = RecommendationsUiState.LoadRecommendations(
                    recommendations = listOf()
                )
            }
        }
    }

    fun onVideoRecorded(tempUri: Uri?) {
        Log.d("SellerCustomerRecommendationsViewModel.onVideoRecorded", "tempUri $tempUri")
        if (tempUri == null) {
            _uiState.value = RecommendationsUiState.Idle(message = "Grabación cancelada o fallida.")
            return
        }


        viewModelScope.launch {
            val sequentialVideoName = "video_${videoCounter}.mp4"
            Log.d("ViewModel", "Guardando video: $sequentialVideoName desde URI: $tempUri")

            val saveResult = videoRepository.saveVideo(tempUri, sequentialVideoName)

            saveResult.onSuccess { savedUri ->
                Log.i("ViewModel", "Video guardado correctamente. URI: $savedUri")
                videoCounter++
                withContext(Dispatchers.Main) {
                    _uiState.value = RecommendationsUiState.Preview(
                        videoUri = savedUri,
                        videoName = sequentialVideoName,
                        message = "Vídeo guardado exitosamente"
                    )
                }

            }
            saveResult.onFailure { exception ->
                Log.e("ViewModel", "Error guardando video", exception)
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

                if (deleteResult.isSuccess) {
                    Log.i("ViewModel", "Video deletion successful via repository.")
                    loadInitialData() // ✅ recargar recomendaciones
                } else {
                    Log.e("ViewModel", "Video deletion failed via repository", deleteResult.exceptionOrNull())
                    _uiState.update {
                        RecommendationsUiState.Idle(
                            showDeleteConfirmDialog = false,
                            message = "Error al eliminar el vídeo: ${deleteResult.exceptionOrNull()?.message}"
                        )
                    }
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
                    loadInitialData()
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
                is RecommendationsUiState.Loading -> RecommendationsUiState.Loading
                is RecommendationsUiState.LoadRecommendations -> currentState.copy()
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

            _uiState.value = RecommendationsUiState.Loading // mostrar spinner temporal

            viewModelScope.launch {
                val uploadResult = videoRepository.uploadVideo(
                    videoFileUri = currentState.videoUri,
                    videoFileName = currentState.videoName,
                    customerId = customerId,
                )

                uploadResult.onSuccess {
                    Log.i("ViewModel", "Subida exitosa: $it")
                    loadInitialData()
                }

                uploadResult.onFailure { exception ->
                    Log.e("ViewModel", "Fallo en la subida", exception)
                    _uiState.value = RecommendationsUiState.Preview(
                        videoUri = currentState.videoUri,
                        videoName = currentState.videoName,
                        message = "Error al solicitar recomendación: ${exception.message}"
                    )
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
                is RecommendationsUiState.LoadRecommendations -> TODO()
            }
        }
    }

    fun onDeleteClick() {
        _uiState.update { currentState ->
            when (currentState) {
                is RecommendationsUiState.Idle -> currentState.copy(showDeleteConfirmDialog = true)
                is RecommendationsUiState.Preview -> currentState.copy(showDeleteConfirmDialog = true)
                RecommendationsUiState.Loading -> TODO()
                is RecommendationsUiState.LoadRecommendations -> TODO()
            }
        }
    }
}


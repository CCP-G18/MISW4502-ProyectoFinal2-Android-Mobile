package com.g18.ccp.presentation.seller.recommendation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.g18.ccp.MainDispatcherRule
import com.g18.ccp.core.constants.CUSTOMER_ID_ARG
import com.g18.ccp.data.remote.model.recommendation.VideoUploadData
import com.g18.ccp.data.remote.model.recommendation.VideoUploadResponse
import com.g18.ccp.repository.seller.videorecommendation.VideoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class SellerCustomerRecommendationsViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var videoRepository: VideoRepository

    private lateinit var viewModel: SellerCustomerRecommendationsViewModel
    private lateinit var savedStateHandle: SavedStateHandle

    private val testCustomerId = "cust-abc"
    private val mockUri: Uri = mockk()
    private val mockSavedUri: Uri = mockk()

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any<String>()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        savedStateHandle = SavedStateHandle(mapOf(CUSTOMER_ID_ARG to testCustomerId))
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        unmockkAll()
    }

    private fun createViewModel() {
        viewModel = SellerCustomerRecommendationsViewModel(savedStateHandle, videoRepository)
    }

    @Test(expected = IllegalStateException::class)
    fun `given SavedStateHandle Without CustomerId - when ViewModel Initializes - then Throws IllegalStateException`() {
        savedStateHandle = SavedStateHandle()
        createViewModel()
    }

    @Test
    fun `when ViewModel Initializes - then Initial State Is Idle`() =
        runTest(mainDispatcherRule.testDispatcher) {
            createViewModel()
            val initialState = viewModel.uiState.value
            assertTrue(initialState is RecommendationsUiState.Idle)
            assertFalse((initialState as RecommendationsUiState.Idle).showDeleteConfirmDialog)
            assertNull(initialState.message)
        }

    @Test
    fun `given Null Uri - when onVideoRecorded - then State Is Idle With Error Message`() =
        runTest(mainDispatcherRule.testDispatcher) {
            createViewModel()
            viewModel.onVideoRecorded(null)
            advanceUntilIdle()
            val state = viewModel.uiState.value
            assertTrue(state is RecommendationsUiState.Idle)
            assertEquals(
                "Grabación cancelada o fallida.",
                (state as RecommendationsUiState.Idle).message
            )
        }

    @Test
    fun `given Valid Uri And Save Success - when onVideoRecorded - then State Is Preview With Correct Data`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val expectedName = "video_1.mp4"
            coEvery { videoRepository.saveVideo(any(), any()) } returns Result.success(
                mockSavedUri
            )
            createViewModel()

            viewModel.onVideoRecorded(mockUri)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is RecommendationsUiState.Preview)
            assertEquals(mockSavedUri, (state as RecommendationsUiState.Preview).videoUri)
            assertEquals(expectedName, state.videoName)
            assertEquals("Vídeo guardado exitosamente", state.message)
            coVerify(exactly = 1) { videoRepository.saveVideo(mockUri, expectedName) }

            // Check counter incremented
            viewModel.onVideoRecorded(mockUri) // Call again
            advanceUntilIdle()
            coVerify(exactly = 1) {
                videoRepository.saveVideo(
                    mockUri,
                    "video_2.mp4"
                )
            } // Verify name uses incremented counter
        }

    @Test
    fun `given Valid Uri And Save Failure - when onVideoRecorded - then State Is Idle With Error Message`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val expectedName = "video_1.mp4"
            val exception = IOException("Disk full")
            coEvery { videoRepository.saveVideo(mockUri, expectedName) } returns Result.failure(
                exception
            )
            createViewModel()

            viewModel.onVideoRecorded(mockUri)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is RecommendationsUiState.Idle)
            assertEquals(
                "Error al guardar el vídeo: ${exception.message}",
                (state as RecommendationsUiState.Idle).message
            )
            coVerify(exactly = 1) { videoRepository.saveVideo(mockUri, expectedName) }
        }

    @Test
    fun `given State Is Preview And Delete Success - when onConfirmDelete - then State Is LoadRecommendations`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val expectedName = "video_1.mp4"
            coEvery { videoRepository.saveVideo(mockUri, expectedName) } returns Result.success(mockSavedUri)
            coEvery { videoRepository.deleteVideo(mockSavedUri) } returns Result.success(Unit)
            coEvery { videoRepository.getRecommendations(testCustomerId) } returns Result.success(emptyList())

            createViewModel()
            viewModel.onVideoRecorded(mockUri)
            advanceUntilIdle()
            viewModel.clearMessage()
            advanceUntilIdle()

            viewModel.onConfirmDelete()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is RecommendationsUiState.LoadRecommendations)
        }

    @Test
    fun `given State Is Preview And Delete Failure - when onConfirmDelete - then State Is Idle With Error Message`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val expectedName = "video_1.mp4"
            val exception = IOException("Cannot delete")
            coEvery { videoRepository.saveVideo(mockUri, expectedName) } returns Result.success(
                mockSavedUri
            )
            coEvery { videoRepository.deleteVideo(mockSavedUri) } returns Result.failure(exception)
            createViewModel()
            viewModel.onVideoRecorded(mockUri)
            advanceUntilIdle()
            viewModel.clearMessage()
            advanceUntilIdle()

            viewModel.onConfirmDelete()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is RecommendationsUiState.Idle)
            assertEquals(
                "Error al eliminar el vídeo: ${exception.message}",
                (state as RecommendationsUiState.Idle).message
            )
            coVerify(exactly = 1) { videoRepository.deleteVideo(mockSavedUri) }
        }

    @Test
    fun `given State Is Idle - when onConfirmDelete - then State Remains Idle And Delete Not Called`() =
        runTest(mainDispatcherRule.testDispatcher) {
            createViewModel()
            viewModel.onConfirmDelete() // Call delete when Idle
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is RecommendationsUiState.Idle)
            assertFalse((state as RecommendationsUiState.Idle).showDeleteConfirmDialog) // Dialog should be hidden
            coVerify(exactly = 0) { videoRepository.deleteVideo(any()) }
        }

    @Test
    fun `given State Is Preview And Delete Success when onCancelPreviewClick then State Is LoadRecommendations And Delete Called`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val expectedName = "video_1.mp4"
            coEvery { videoRepository.saveVideo(mockUri, expectedName) } returns Result.success(mockSavedUri)
            coEvery { videoRepository.deleteVideo(mockSavedUri) } returns Result.success(Unit)
            coEvery { videoRepository.getRecommendations(testCustomerId) } returns Result.success(emptyList())

            createViewModel()
            viewModel.onVideoRecorded(mockUri)
            advanceUntilIdle()

            viewModel.onCancelPreviewClick()
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value is RecommendationsUiState.LoadRecommendations)
            coVerify(exactly = 1) { videoRepository.deleteVideo(mockSavedUri) }
        }


    @Test
    fun `given State Is Preview And Delete Fails when onCancelPreviewClick then State Remains Preview With Error`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val expectedName = "video_1.mp4"
            val exception = IOException("Delete failed")
            coEvery { videoRepository.saveVideo(mockUri, expectedName) } returns Result.success(
                mockSavedUri
            )
            coEvery { videoRepository.deleteVideo(mockSavedUri) } returns Result.failure(exception)
            createViewModel()
            viewModel.onVideoRecorded(mockUri)
            advanceUntilIdle()
            viewModel.clearMessage() // Clear initial save message
            advanceUntilIdle()

            viewModel.onCancelPreviewClick()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is RecommendationsUiState.Preview)
            assertEquals(mockSavedUri, (state as RecommendationsUiState.Preview).videoUri)
            assertEquals(expectedName, state.videoName)
            assertEquals("Error al borrar el vídeo temporal: ${exception.message}", state.message)
            coVerify(exactly = 1) { videoRepository.deleteVideo(mockSavedUri) }
        }

    @Test
    fun `given State Is Idle - when onCancelPreviewClick - then State Remains Idle`() =
        runTest(mainDispatcherRule.testDispatcher) {
            createViewModel()
            val initialState = viewModel.uiState.value

            viewModel.onCancelPreviewClick()
            advanceUntilIdle()

            assertEquals(initialState, viewModel.uiState.value) // Should still be Idle
            coVerify(exactly = 0) { videoRepository.deleteVideo(any()) }
        }

    @Test
    fun `when onDeleteClick - then State Shows Delete Confirm Dialog`() =
        runTest(mainDispatcherRule.testDispatcher) {
            createViewModel()

            viewModel.onDeleteClick()
            advanceUntilIdle()
            var state = viewModel.uiState.value
            assertTrue(state is RecommendationsUiState.Idle)
            assertTrue((state as RecommendationsUiState.Idle).showDeleteConfirmDialog)

            val expectedName = "video_1.mp4"
            coEvery { videoRepository.saveVideo(mockUri, expectedName) } returns Result.success(
                mockSavedUri
            )
            viewModel.onVideoRecorded(mockUri)
            advanceUntilIdle()

            viewModel.onDeleteClick()
            advanceUntilIdle()
            state = viewModel.uiState.value
            assertTrue(state is RecommendationsUiState.Preview)
            assertTrue((state as RecommendationsUiState.Preview).showDeleteConfirmDialog)
        }

    @Test
    fun `when onCancelDelete - then State Hides Delete Confirm Dialog`() =
        runTest(mainDispatcherRule.testDispatcher) {
            createViewModel()

            viewModel.onDeleteClick()
            advanceUntilIdle()
            assertTrue((viewModel.uiState.value as RecommendationsUiState.Idle).showDeleteConfirmDialog)

            viewModel.onCancelDelete()
            advanceUntilIdle()
            assertFalse((viewModel.uiState.value as RecommendationsUiState.Idle).showDeleteConfirmDialog)

            val expectedName = "video_1.mp4"
            coEvery { videoRepository.saveVideo(mockUri, expectedName) } returns Result.success(
                mockSavedUri
            )
            viewModel.onVideoRecorded(mockUri)
            advanceUntilIdle()
            viewModel.onDeleteClick()
            advanceUntilIdle()
            assertTrue((viewModel.uiState.value as RecommendationsUiState.Preview).showDeleteConfirmDialog)

            viewModel.onCancelDelete()
            advanceUntilIdle()
            assertFalse((viewModel.uiState.value as RecommendationsUiState.Preview).showDeleteConfirmDialog)
        }

    @Test
    fun `when simulateCameraError - then State Is Idle With Error Message`() =
        runTest(mainDispatcherRule.testDispatcher) {
            createViewModel()
            viewModel.simulateCameraError()
            advanceUntilIdle()
            val state = viewModel.uiState.value
            assertTrue(state is RecommendationsUiState.Idle)
            assertEquals(
                "Error: Cámara no disponible o permiso denegado.",
                (state as RecommendationsUiState.Idle).message
            )
        }

    @Test
    fun `when clearMessage - then Message In State Is Null`() =
        runTest(mainDispatcherRule.testDispatcher) {
            createViewModel()
            viewModel.simulateCameraError() // Set an initial message
            advanceUntilIdle()
            assertTrue((viewModel.uiState.value as RecommendationsUiState.Idle).message != null)

            viewModel.clearMessage()
            advanceUntilIdle()
            assertTrue((viewModel.uiState.value as RecommendationsUiState.Idle).message == null)

            val expectedName = "video_1.mp4"
            coEvery { videoRepository.saveVideo(mockUri, expectedName) } returns Result.success(
                mockSavedUri
            )
            viewModel.onVideoRecorded(mockUri)
            advanceUntilIdle()
            assertTrue((viewModel.uiState.value as RecommendationsUiState.Preview).message != null)

            viewModel.clearMessage()
            advanceUntilIdle()
            assertTrue((viewModel.uiState.value as RecommendationsUiState.Preview).message == null)
        }

    @Test
    fun `given Is Preview And Upload Success when onReceiveRecommendationClick then State Is LoadRecommendations`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val expectedName = "video_1.mp4"
            coEvery { videoRepository.saveVideo(mockUri, expectedName) } returns Result.success(mockSavedUri)

            val uploadData = VideoUploadData(
                createdAt = "2025-05-07T12:00:00Z",
                customerId = testCustomerId,
                id = "upload-123",
                sellerId = "seller-456",
                updatedAt = "2025-05-07T12:00:00Z",
                video = "video_1.mp4"
            )
            val fakeResponse = VideoUploadResponse(
                code = 200,
                data = uploadData,
                message = "OK",
                status = "success"
            )

            coEvery {
                videoRepository.uploadVideo(mockSavedUri, expectedName, testCustomerId)
            } returns Result.success(fakeResponse)

            coEvery {
                videoRepository.getRecommendations(testCustomerId)
            } returns Result.success(emptyList())

            createViewModel()
            viewModel.onVideoRecorded(mockUri)
            advanceUntilIdle()
            viewModel.clearMessage()
            advanceUntilIdle()

            viewModel.onReceiveRecommendationClick()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state is RecommendationsUiState.LoadRecommendations)
            coVerify { videoRepository.uploadVideo(mockSavedUri, expectedName, testCustomerId) }
        }


    @Test
    fun `given Is Preview And Upload Failure when onReceiveRecommendationClick then Is Preview With Error Message`() =
        runTest(mainDispatcherRule.testDispatcher) {
            // given: stub de saveVideo para llegar a Preview
            val expectedName = "video_1.mp4"
            coEvery { videoRepository.saveVideo(mockUri, expectedName) } returns Result.success(
                mockSavedUri
            )

            // stub de uploadVideo que falla
            val exception = IOException("Network down")
            coEvery {
                videoRepository.uploadVideo(
                    mockSavedUri,
                    expectedName,
                    testCustomerId
                )
            } returns Result.failure(exception)

            createViewModel()
            viewModel.onVideoRecorded(mockUri)
            advanceUntilIdle()
            viewModel.clearMessage()
            advanceUntilIdle()

            // when
            viewModel.onReceiveRecommendationClick()
            advanceUntilIdle()

            // then: se invoca al repositorio
            coVerify(exactly = 1) {
                videoRepository.uploadVideo(
                    mockSavedUri,
                    expectedName,
                    testCustomerId
                )
            }
            // then: el estado final muestra el mensaje de error con exception.message
            val state = viewModel.uiState.value as RecommendationsUiState.Preview
            assertEquals(
                "Error al solicitar recomendación: ${exception.message}",
                state.message
            )
        }

    @Test
    fun `given State Is Idle - when onReceiveRecommendationClick - then State Remains Idle And Upload Not Called`() =
        runTest(mainDispatcherRule.testDispatcher) {
            // given: ViewModel recién creado → Idle
            createViewModel()
            val initial = viewModel.uiState.value

            // when
            viewModel.onReceiveRecommendationClick()
            advanceUntilIdle()

            // then: no cambia el estado
            assertEquals(initial, viewModel.uiState.value)
            // y no se llama a uploadVideo
            coVerify(exactly = 0) {
                videoRepository.uploadVideo(any(), any(), any())
            }
        }


    @Test
    fun `when onReceiveRecommendationClick - then State Does Not Change If Not Preview`() =
        runTest(mainDispatcherRule.testDispatcher) {
            createViewModel()
            val initialState = viewModel.uiState.value
            assertTrue(initialState is RecommendationsUiState.Idle)

            viewModel.onReceiveRecommendationClick()
            advanceUntilIdle()

            assertEquals(initialState, viewModel.uiState.value) // State remains Idle
        }
}

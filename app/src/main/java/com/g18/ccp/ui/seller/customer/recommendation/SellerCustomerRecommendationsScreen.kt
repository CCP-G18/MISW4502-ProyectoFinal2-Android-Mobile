package com.g18.ccp.ui.seller.customer.recommendation

import android.Manifest
import android.net.Uri
import android.util.Log
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.g18.ccp.R
import com.g18.ccp.presentation.seller.recommendation.RecommendationsUiState
import com.g18.ccp.presentation.seller.recommendation.SellerCustomerRecommendationsViewModel
import com.g18.ccp.ui.theme.BackgroundColor
import com.g18.ccp.ui.theme.BlackColor
import com.g18.ccp.ui.theme.ErrorColor
import com.g18.ccp.ui.theme.MainColor
import com.g18.ccp.ui.theme.WhiteColor
import java.io.File

@Composable
fun SellerCustomerRecommendationsScreen(
    modifier: Modifier = Modifier,
    viewModel: SellerCustomerRecommendationsViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var videoUriToSave by remember { mutableStateOf<Uri?>(null) }

    val videoCaptureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
        onResult = { success: Boolean ->
            val savedUri = videoUriToSave
            if (success && savedUri != null) {
                Log.d("RecommendScreen", "Video capture success. URI: $savedUri")
                viewModel.onVideoRecorded(savedUri)
            } else {
                Log.d("RecommendScreen", "Video capture failed or cancelled.")
                viewModel.onVideoRecorded(null)
            }
            videoUriToSave = null
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val newVideoUri: Uri? =
                    try {
                        val file =
                            File(context.filesDir, "temp_video_${System.currentTimeMillis()}.mp4")
                        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    } catch (e: Exception) {
                        Log.e("RecommendScreen", "Error creating file URI", e)
                        null
                    }

                if (newVideoUri != null) {
                    videoUriToSave = newVideoUri
                    Log.d("RecommendScreen", "URI created: $newVideoUri. Launching camera...")
                    videoCaptureLauncher.launch(newVideoUri)
                } else {
                    viewModel.simulateCameraError()
                }
            } else {
                viewModel.simulateCameraError()
                Log.d("RecommendScreen", "Camera permission denied.")
            }
        }
    )

    LaunchedEffect(uiState) {
        val message = when (val state = uiState) {
            is RecommendationsUiState.Idle -> state.message
            is RecommendationsUiState.Preview -> state.message
            RecommendationsUiState.Loading -> TODO()
            is RecommendationsUiState.Success -> TODO()
        }
        if (message != null) {
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
            viewModel.clearMessage()
        }
    }

    val showDialog = when (val state = uiState) {
        is RecommendationsUiState.Idle -> state.showDeleteConfirmDialog
        is RecommendationsUiState.Preview -> state.showDeleteConfirmDialog
        RecommendationsUiState.Loading -> TODO()
        is RecommendationsUiState.Success -> TODO()
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onCancelDelete() },
            title = { Text(stringResource(R.string.confirm_delete_title)) },
            text = { Text(stringResource(R.string.confirm_delete_message)) },
            confirmButton = {
                Button(
                    onClick = { viewModel.onConfirmDelete() },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorColor)
                ) { Text(stringResource(R.string.delete), color = WhiteColor) }
            },
            dismissButton = {
                Button(
                    onClick = { viewModel.onCancelDelete() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainColor,
                        contentColor = BackgroundColor
                    )
                ) { Text(stringResource(R.string.cancel)) }
            }
        )
    }


    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = uiState) {
                is RecommendationsUiState.Idle -> {
                    IdleContent {
                        Log.d("RecommendScreen", "Record button clicked. Requesting permission...")
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }

                is RecommendationsUiState.Preview -> {
                    PreviewContent(
                        videoUri = state.videoUri,
                        videoName = state.videoName,
                        onDeleteClick = { viewModel.onDeleteClick() },
                        onCancelClick = { viewModel.onCancelPreviewClick() },
                        onReceiveClick = { viewModel.onReceiveRecommendationClick() }
                    )
                }

                RecommendationsUiState.Loading -> TODO()
                is RecommendationsUiState.Success -> TODO()
            }
        }
    }
}


@Composable
private fun IdleContent(onRecordClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(MainColor, CircleShape)
                .clickable(onClick = onRecordClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Videocam,
                contentDescription = stringResource(R.string.record_video_desc),
                tint = WhiteColor,
                modifier = Modifier.size(60.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.record_store_prompt),
            textAlign = TextAlign.Center,
            color = MainColor
        )
    }
}

@Composable
private fun PreviewContent(
    videoUri: Uri,
    videoName: String,
    onDeleteClick: () -> Unit,
    onCancelClick: () -> Unit,
    onReceiveClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            AndroidView(
                factory = { context ->
                    VideoView(context).apply {
                        setVideoURI(videoUri)
                        setOnPreparedListener { mediaPlayer ->
                            mediaPlayer.isLooping = true
                            mediaPlayer.setVolume(0f, 0f)
                        }
                        requestFocus()
                        start()
                    }
                },
                modifier = Modifier
                    .size(width = 130.dp, height = 90.dp)
                    .background(Color.DarkGray)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = videoName,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.Medium,
                    color = BlackColor,
                    modifier = Modifier.padding(bottom = 8.dp),
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )

                Text(
                    text = stringResource(R.string.delete),
                    color = ErrorColor,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                    modifier = Modifier
                        .clickable(onClick = onDeleteClick)
                        .padding(vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onCancelClick,
                colors = ButtonDefaults.buttonColors(containerColor = ErrorColor)
            ) {
                Text(stringResource(R.string.cancel), color = WhiteColor)
            }
            Button(
                onClick = onReceiveClick,
                colors = ButtonDefaults.buttonColors(containerColor = MainColor)
            ) {
                Text(stringResource(R.string.receive_recommendation), color = WhiteColor)
            }
        }
    }
}

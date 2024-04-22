package com.example.cameraxdemo.ui

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import com.example.cameraxdemo.camera.QrImageAnalyzer

@Composable
fun CameraScreen(
    cameraViewModel: CameraViewModel,
    requestPermission: () -> Unit
) {
    val cameraUiState by cameraViewModel.cameraUiState.collectAsState()

    if (cameraUiState.isPermissionGranted) {
        CameraLayout()
    } else {
        UnauthorizedCameraLayout(
            requestPermission = { requestPermission() }
        )
    }
}

@Composable
fun CameraLayout() {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    LaunchedEffect(lensFacing) {
        val qrImageAnalyzer = QrImageAnalyzer()
        val imageAnalyzer = ImageAnalysis.Builder()
            .build().also {
                it.setAnalyzer(ContextCompat.getMainExecutor(context), qrImageAnalyzer)
            }
        ProcessCameraProvider.getInstance(context).await().also {
            it.unbindAll()
            val camera = it.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )
            qrImageAnalyzer.setCamera(camera)
        }

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }
    AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
}

@Composable
fun UnauthorizedCameraLayout(
    requestPermission: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { requestPermission() }) {
            Text(text = "Request Camera Permission")
        }
    }
}

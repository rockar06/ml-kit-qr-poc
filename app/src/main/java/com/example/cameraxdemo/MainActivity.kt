package com.example.cameraxdemo

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.cameraxdemo.ui.CameraScreen
import com.example.cameraxdemo.ui.CameraViewModel
import com.example.cameraxdemo.ui.CameraViewModelFactory
import com.example.cameraxdemo.ui.theme.CameraXDemoTheme

class MainActivity : ComponentActivity() {

    private val cameraViewModel by viewModels<CameraViewModel> {
        CameraViewModelFactory(PermissionUtils(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraXDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CameraScreen(
                        cameraViewModel = cameraViewModel,
                        requestPermission = {
                            cameraPermissionRequest.launch(
                                Manifest.permission.CAMERA
                            )
                        }
                    )
                }
            }
        }
    }


    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraViewModel.setCameraPermissionGranted()
            } else {
                cameraViewModel.setCameraPermissionDenied()
            }
        }
}

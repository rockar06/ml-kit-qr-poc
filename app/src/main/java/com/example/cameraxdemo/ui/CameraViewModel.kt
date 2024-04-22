package com.example.cameraxdemo.ui

import android.Manifest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cameraxdemo.PermissionUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CameraViewModel(
    private val permissionUtils: PermissionUtils
) : ViewModel() {

    private val _cameraUiState = MutableStateFlow(CameraUiState())
    val cameraUiState = _cameraUiState.asStateFlow()

    init {
        verifyCameraPermission()
    }

    private fun verifyCameraPermission() {
        if (permissionUtils.isPermissionGranted(Manifest.permission.CAMERA)) {
            updateCameraPermission(true)
        }
    }

    fun setCameraPermissionGranted() {
        updateCameraPermission(true)
    }

    fun setCameraPermissionDenied() {
        updateCameraPermission(false)
    }

    private fun updateCameraPermission(isGranted: Boolean) {
        _cameraUiState.update { currentState ->
            currentState.copy(isPermissionGranted = isGranted)
        }
    }


}

class CameraViewModelFactory(private val permissionUtils: PermissionUtils) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CameraViewModel(permissionUtils) as T
    }
}

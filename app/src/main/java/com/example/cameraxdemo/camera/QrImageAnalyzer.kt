package com.example.cameraxdemo.camera

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraState
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.ZoomSuggestionOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrImageAnalyzer : ImageAnalysis.Analyzer {

    companion object {
        val TAG = QrImageAnalyzer::class.simpleName
    }

    private var camera: Camera? = null
    private lateinit var scanner: BarcodeScanner

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (camera == null) return
        imageProxy.image?.let {
            val image = InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)
            val result = scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    barcodes.forEach { barcode ->
                        val bounds = barcode.boundingBox
                        val corners = barcode.cornerPoints
                        val rawValue = barcode.rawValue
                        val valueType = barcode.valueType

                        when (valueType) {
                            Barcode.TYPE_WIFI -> {
                                val ssid = barcode.wifi?.ssid.orEmpty()
                                val password = barcode.wifi?.password.orEmpty()
                                val type = barcode.wifi?.encryptionType ?: 0
                                Log.d(TAG, "WiFi tag detected")
                            }

                            Barcode.TYPE_URL -> {
                                val title = barcode.url?.title.orEmpty()
                                val url = barcode.url?.url.orEmpty()
                                Log.d(TAG, "URL tag detected")
                                Log.d(TAG, "URL is: $url")
                            }
                        }
                    }
                    imageProxy.close()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "There was a problem while processing image", exception)
                    imageProxy.close()
                }
        }
    }

    fun setCamera(camera: Camera) {
        configureScanner(camera)
        this.camera = camera
    }

    private fun configureScanner(camera: Camera) {
        scanner = BarcodeScanning.getClient(getScannerOptions(camera))
    }

    private fun getScannerOptions(camera: Camera): BarcodeScannerOptions =
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            )
            .enableAllPotentialBarcodes()
            .setZoomSuggestionOptions(ZoomSuggestionOptions.Builder {
                if (camera.cameraInfo.cameraState.value?.type != CameraState.Type.OPEN) {
                    false
                } else {
                    camera.cameraControl.setZoomRatio(it)
                    true
                }
            }
                .setMaxSupportedZoomRatio(camera.cameraInfo.zoomState.value?.maxZoomRatio ?: 0.0f)
                .build())
            .build()
}

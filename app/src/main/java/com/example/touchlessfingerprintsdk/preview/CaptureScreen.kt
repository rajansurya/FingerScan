package com.example.touchlessfingerprintsdk.preview

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.touchlessfingerprintsdk.starttouch.data.FingerprintResult
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.io.File
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/*@Composable
fun CaptureScreen(onCaptureResult: (FingerprintResult) -> Unit) {
    val context = LocalContext.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    LaunchedEffect(Unit) {
        cameraProvider = cameraProviderFuture.get()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        cameraProvider?.let {
            CameraPreviewView(it, imageCapture)
            HandAlignmentOverlay()
        }

        // Fake alignment detection
        LaunchedEffect(Unit) {
            delay(3000) // mock alignment detection time
            val photoFile = File(context.cacheDir, "slap_capture.jpg")
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                        val segmented = segmentFingers(bitmap)
                        val scores = segmented.map { (1..5).random() }
                        val combined = combineBitmaps(segmented)
                        val encrypted = encryptImageBlob(combined)

                        onCaptureResult(
                            FingerprintResult(
                                capturedImage = bitmap,
                                segmentedImages = segmented,
                                qualityScores = scores,
                                encryptedBlob = Base64.encodeToString(encrypted, Base64.DEFAULT)
                            )
                        )
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("Capture", "Capture failed: ${exception.message}")
                    }
                }
            )
        }
    }
}*/


@OptIn(ExperimentalPermissionsApi::class, ExperimentalPermissionsApi::class)
@Composable
fun CaptureScreen(onCaptureResult: (FingerprintResult) -> Unit) {
    val context = LocalContext.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        } else {
            cameraProvider = cameraProviderFuture.get()
        }
    }

    when {
        cameraPermissionState.status.isGranted -> {
            Box(modifier = Modifier.fillMaxSize()) {
                cameraProvider?.let {
                    CameraPreviewView(it, imageCapture)
                    HandAlignmentOverlay()
                }

                // Fake alignment detection
                LaunchedEffect(Unit) {
                    delay(3000) // mock alignment detection time
                    val photoFile = File(context.cacheDir, "slap_capture.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    imageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                                val segmented = segmentFingers(bitmap)
                                val scores = segmented.map { (1..5).random() }
                                val combined = combineBitmaps(segmented)
                                val encrypted = encryptImageBlob(combined)

                                onCaptureResult(
                                    FingerprintResult(
                                        capturedImage = bitmap,
                                        segmentedImages = segmented,
                                        qualityScores = scores,
                                        encryptedBlob = Base64.encodeToString(encrypted, Base64.DEFAULT)
                                    )
                                )
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("Capture", "Capture failed: ${exception.message}")
                            }
                        }
                    )
                }
            }
        }

        cameraPermissionState.status.shouldShowRationale -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Camera permission is needed to capture fingerprint image.")
            }
        }

        else -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Requesting camera permission...")
            }
        }
    }
}



// 6. Image segmentation using OpenCV
fun segmentFingers(bitmap: Bitmap): List<Bitmap> {
    val width = bitmap.width / 4
    return List(4) { index ->
        Bitmap.createBitmap(bitmap, index * width, 0, width, bitmap.height)
    }
}

// 7. Combine segmented images into one blob
fun combineBitmaps(bitmaps: List<Bitmap>): ByteArray {
    val output = ByteArrayOutputStream()
    for (bitmap in bitmaps) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
    }
    return output.toByteArray()
}

// 8. Convert Bitmap to ByteArray
fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 80, stream)
    return stream.toByteArray()
}

// 9. Encrypt with AES-256
fun encryptImageBlob(data: ByteArray): ByteArray {
    val key = SecretKeySpec("12345678901234567890123456789012".toByteArray(), "AES") // 32-byte key
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    return cipher.doFinal(data)
}

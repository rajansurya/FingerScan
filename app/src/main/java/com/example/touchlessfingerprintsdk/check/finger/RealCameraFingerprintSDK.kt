package com.example.touchlessfingerprintsdk.check.finger

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

class RealCameraFingerprintSDK(private val imageCapture: ImageCapture) : TouchlessFingerprintSDK {

    override suspend fun captureAndProcess(activity: Activity): FingerprintResult {
        val bitmap = captureImage(activity, imageCapture)
        val segmented = FingerSegmenter.segmentImage(bitmap)
        val scores = segmented.map { (1..5).random() }
        val encrypted = AESUtil.encryptBitmap(bitmap)
        return FingerprintResult(bitmap, segmented, scores, encrypted)
    }

    private suspend fun captureImage(context: Context, imageCapture: ImageCapture): Bitmap {
        return suspendCancellableCoroutine { cont ->
            val photoFile = File.createTempFile("fingerprint_", ".jpg", context.cacheDir)
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        cont.cancel(exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            context.contentResolver,
                            output.savedUri!!
                        )
                        cont.resume(bitmap, onCancellation = null)
                    }
                }
            )
        }
    }
}
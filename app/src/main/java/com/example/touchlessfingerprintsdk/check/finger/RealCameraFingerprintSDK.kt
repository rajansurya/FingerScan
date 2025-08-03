package com.example.touchlessfingerprintsdk.check.finger

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.impl.utils.MatrixExt.postRotate
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
                        val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                        val inputStream = context.contentResolver.openInputStream(savedUri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)

                        // Get EXIF rotation
                        val exif = ExifInterface(photoFile.absolutePath)
                        val rotationDegrees = when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                            ExifInterface.ORIENTATION_ROTATE_90 -> 90
                            ExifInterface.ORIENTATION_ROTATE_180 -> 180
                            ExifInterface.ORIENTATION_ROTATE_270 -> 270
                            else -> 0
                        }

                        // Rotate the bitmap if needed
                        val rotatedBitmap = if (rotationDegrees != 0) {
                            val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
                            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                        } else {
                            bitmap
                        }

                        cont.resume(rotatedBitmap, onCancellation = null)




                           /* val bitmap = MediaStore.Images.Media.getBitmap(
                                context.contentResolver,
                                output.savedUri!!
                            )
                            cont.resume(bitmap, onCancellation = null)*/
                    }
                }
            )
        }
    }
}
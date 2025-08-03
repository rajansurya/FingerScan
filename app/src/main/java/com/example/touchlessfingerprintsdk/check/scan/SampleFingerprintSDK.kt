package com.example.touchlessfingerprintsdk.check.scan

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class SampleFingerprintSDK : TouchlessFingerprintSDK {
    override suspend fun captureAndProcess(activity: Activity): FingerprintResult {
        val capturedImage = generateFakeHandBitmap()
        val segmentedImages = FingerSegmenter.segmentImage(capturedImage)
        val qualityScores = segmentedImages.map { (1..5).random() }
        val encryptedBlob = AESUtil.encryptBitmap(capturedImage)
        return FingerprintResult(capturedImage, segmentedImages, qualityScores, encryptedBlob)
    }

    private fun generateFakeHandBitmap(): Bitmap {
        val bmp = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }
        for (i in 0..3) {
            canvas.drawRect((i * 100).toFloat(), 0f, (i + 1) * 100f, 100f, paint)
        }
        return bmp
    }
}
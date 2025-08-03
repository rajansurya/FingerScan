package com.example.touchlessfingerprintsdk.check.finger

import android.graphics.Bitmap

data class FingerprintResult(
    val capturedImage: Bitmap,
    val segmentedImages: List<Bitmap>,
    val qualityScores: List<Int>,
    val encryptedBlob: String
)
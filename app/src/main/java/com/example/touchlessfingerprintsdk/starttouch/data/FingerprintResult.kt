package com.example.touchlessfingerprintsdk.starttouch.data

import android.graphics.Bitmap

data class FingerprintResult(
    val capturedImage: Bitmap,
    val segmentedImages: List<Bitmap>,
    val qualityScores: List<Int>, // Simulated values
    val encryptedBlob: String // Encrypted Base64 string
)
package com.example.touchlessfingerprintsdk.check.scan

import android.app.Activity


interface TouchlessFingerprintSDK {
    suspend fun captureAndProcess(activity: Activity): FingerprintResult
}
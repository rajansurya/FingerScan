package com.example.touchlessfingerprintsdk.check.finger
import android.app.Activity

interface TouchlessFingerprintSDK {
    suspend fun captureAndProcess(activity: Activity): FingerprintResult
}
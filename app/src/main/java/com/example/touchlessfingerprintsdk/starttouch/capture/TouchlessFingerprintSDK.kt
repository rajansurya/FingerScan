package com.example.touchlessfingerprintsdk.starttouch.capture

import android.app.Activity
import com.example.touchlessfingerprintsdk.starttouch.data.FingerprintResult

interface TouchlessFingerprintSDK {
    suspend fun captureAndProcess(activity: Activity): FingerprintResult
}
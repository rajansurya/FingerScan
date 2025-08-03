package com.example.touchlessfingerprintsdk.check.finger

import android.app.Activity
import androidx.camera.core.ImageCapture
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FingerprintViewModelFactory(
    private val activity: Activity,
    private val imageCapture: ImageCapture
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FingerprintViewModel::class.java)) {
            val sdk = RealCameraFingerprintSDK(imageCapture)
            @Suppress("UNCHECKED_CAST")
            return FingerprintViewModel(activity, sdk) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
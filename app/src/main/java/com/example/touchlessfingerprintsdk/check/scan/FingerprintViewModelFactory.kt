package com.example.touchlessfingerprintsdk.check.scan

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider



class FingerprintViewModelFactory(private val activity: Activity) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FingerprintViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FingerprintViewModel(activity) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
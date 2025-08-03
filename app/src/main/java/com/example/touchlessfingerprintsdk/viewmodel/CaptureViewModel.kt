package com.example.touchlessfingerprintsdk.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.touchlessfingerprintsdk.starttouch.capture.TouchlessFingerprintSDK
import com.example.touchlessfingerprintsdk.starttouch.data.FingerprintResult
import kotlinx.coroutines.launch

class CaptureViewModel(
    private val fingerprintSDK: TouchlessFingerprintSDK
) : ViewModel() {

    private val _result = MutableLiveData<FingerprintResult?>()
    val result: LiveData<FingerprintResult?> = _result

    fun startFingerprintCapture(activity: Activity) {
        viewModelScope.launch {
            val result = fingerprintSDK.captureAndProcess(activity)
            _result.value = result
        }
    }
}

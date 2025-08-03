package com.example.touchlessfingerprintsdk.check.finger

import android.app.Activity
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FingerprintViewModel(
    private val activity: Activity,
    private val sdk: TouchlessFingerprintSDK
) : ViewModel() {

    private val _result = mutableStateOf<FingerprintResult?>(null)
    private val _loading = mutableStateOf(false)

    val result: State<FingerprintResult?> get() = _result
    val loading: State<Boolean> get() = _loading

    fun capture() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            _result.value = sdk.captureAndProcess(activity)
            _loading.value = false
        }
    }
}
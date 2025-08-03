package com.example.touchlessfingerprintsdk.check.finger
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ScanMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FingerprintApp()
        }
    }
}
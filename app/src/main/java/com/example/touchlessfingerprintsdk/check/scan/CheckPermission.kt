package com.example.touchlessfingerprintsdk.check.scan

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlin.collections.get

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MissingPermission",
    "ContextCastToActivity"
)
@Composable
fun FingerprintApp() {
    val context = LocalContext.current as Activity

    // ViewModel creation using factory
    val viewModel: FingerprintViewModel = viewModel(factory = FingerprintViewModelFactory(context))

    // ✅ Correct: Access the .value directly
    val result = viewModel.result.value
    val loading = viewModel.loading.value

    // Camera permission state
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasPermission = granted
            if (!granted) {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Request permission if not already granted
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                if (hasPermission) {
                    viewModel.capture()
                } else {
                    Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show()
                    launcher.launch(Manifest.permission.CAMERA)
                }
            }
        ) {
            Text("Start Fingerprint Scan")
        }

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        result?.let { res ->
            Text("Captured Image:", style = MaterialTheme.typography.titleMedium)
            Image(
                bitmap = res.capturedImage.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )

            Spacer(Modifier.height(8.dp))
            Text("Finger Segments:", style = MaterialTheme.typography.titleMedium)
            LazyRow {
                items(res.segmentedImages.size) { i ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Image(
                            bitmap = res.segmentedImages[i].asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp)
                        )
                        Text("Score: ${res.qualityScores[i]}")
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Encrypted Base64:", style = MaterialTheme.typography.titleMedium)
            Text(res.encryptedBlob.take(200) + "...")
        }
    }
}

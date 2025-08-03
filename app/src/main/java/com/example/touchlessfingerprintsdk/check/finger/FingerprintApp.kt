package com.example.touchlessfingerprintsdk.check.finger

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.Surface
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File

@SuppressLint("ContextCastToActivity")
@Composable
fun FingerprintApp() {
    val context = LocalContext.current as Activity
    val imageCapture = remember {
        ImageCapture.Builder().setTargetRotation(Surface.ROTATION_0).build()
    }
    val viewModel: FingerprintViewModel = viewModel(
        factory = FingerprintViewModelFactory(context, imageCapture)
    )

    val result = viewModel.result.value
    val loading = viewModel.loading.value

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
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

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        CameraPreviewView(imageCapture = imageCapture)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (hasPermission) {
                    viewModel.capture()
                } else {
                    launcher.launch(Manifest.permission.CAMERA)
                }
            }
        ) {
            Text("Capture Fingerprint")
        }

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        result?.let { res ->
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)) {
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
}


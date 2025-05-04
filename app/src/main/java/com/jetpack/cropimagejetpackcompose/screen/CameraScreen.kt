package com.jetpack.cropimagejetpackcompose.screen

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.jetpack.cropimagejetpackcompose.util.uriToBitmap
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

@Composable
fun CameraScreen(
    onImageCaptured: (Uri, bitmap: Bitmap) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var cameraProvider: ProcessCameraProvider? = null
    val previewView = remember { PreviewView(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

    LaunchedEffect(Unit) {
        cameraProvider = context.getCameraProvider()

        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()

        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        try {
            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture!!
            )
        } catch (e: Exception) {
            Log.e("CameraX", "Binding failed", e)
        }
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Button(onClick = {
            val photoFile = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture?.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        cameraProvider?.unbindAll()
                        val uri = Uri.fromFile(photoFile)
                        val bitmap = uriToBitmap(context, uri)
                        if (bitmap != null)
                            onImageCaptured(uri, bitmap)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("CameraX", "Image capture failed", exception)
                    }
                }
            )
        }) {
            Text("Capture",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center)
        }
    }
}

suspend fun android.content.Context.getCameraProvider(): ProcessCameraProvider =
    suspendCancellableCoroutine { continuation ->
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener(
            { continuation.resume(future.get()) },
            ContextCompat.getMainExecutor(this)
        )
    }


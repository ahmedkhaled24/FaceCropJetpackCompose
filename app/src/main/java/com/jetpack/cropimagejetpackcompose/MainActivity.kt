package com.jetpack.cropimagejetpackcompose

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import com.jetpack.cropimagejetpackcompose.screen.CameraScreen
import com.jetpack.cropimagejetpackcompose.screen.ResultScreen
import com.jetpack.cropimagejetpackcompose.viewmodel.CropImageListener
import com.jetpack.cropimagejetpackcompose.viewmodel.CropImageViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), CropImageListener {

    private lateinit var viewModel: CropImageViewModel

    private val originalImageUri = mutableStateOf<Uri?>(null)
    private val croppedImageBitmap = mutableStateOf<Bitmap?>(null)

    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = CropImageViewModel(this)

        cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when(isGranted) {
                true  ->
                    start()

                false ->
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        Toast.makeText(this, "Camera permission is denied. Please enable it in the app settings.", Toast.LENGTH_LONG).show()
                        openAppSettings()
                    }
            }
        }


        when(checkSelfPermission(Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> start()
            else   /* PERMISSION_DENIED */    -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun start() {
        setContent {
            if (croppedImageBitmap.value != null && originalImageUri.value != null) {
                ResultScreen(
                    originalImageUri = originalImageUri.value!!,
                    croppedImage = croppedImageBitmap.value!!
                )
            } else {
                CameraScreen(
                    onImageCaptured = { uri, bitmap ->
                        originalImageUri.value = uri
                        viewModel.detectImage(bitmap)
                    }
                )
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${packageName}")
        }
        startActivity(intent)
        finish()
    }

    override fun onCroppedSuccess(image: Bitmap) {
        this.lifecycleScope.launch {
            Toast.makeText(this@MainActivity, "Captured Successfully", Toast.LENGTH_SHORT).show()
            croppedImageBitmap.value = image
        }
    }

    override fun onCroppedFailed(error: String) {
        this.lifecycleScope.launch {
            Toast.makeText(this@MainActivity, "Error: $error", Toast.LENGTH_SHORT).show()
        }
    }
}

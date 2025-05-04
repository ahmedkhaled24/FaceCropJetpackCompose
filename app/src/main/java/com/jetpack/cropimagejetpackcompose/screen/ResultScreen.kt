package com.jetpack.cropimagejetpackcompose.screen

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun ResultScreen(originalImageUri: Uri, croppedImage: Bitmap) {
    Row(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Image(
            bitmap = croppedImage.asImageBitmap(),
            contentDescription = "Cropped Image",
            modifier = Modifier.weight(1f).fillMaxHeight().padding(8.dp),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = rememberAsyncImagePainter(originalImageUri),
            contentDescription = "Original Image",
            modifier = Modifier.weight(1f).fillMaxHeight().padding(8.dp),
            contentScale = ContentScale.Crop
        )
    }
}

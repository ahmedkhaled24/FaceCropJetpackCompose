package com.jetpack.cropimagejetpackcompose.viewmodel

import android.graphics.Bitmap
import com.jetpack.cropimagejetpackcompose.features.open_cv.FaceDetection
import com.jetpack.cropimagejetpackcompose.features.face_detection.FaceFeature
import com.jetpack.cropimagejetpackcompose.features.face_detection.MLKitFaceDetection
import org.opencv.android.OpenCVLoader
import org.opencv.core.CvType
import org.opencv.core.Mat

class CropImageViewModel(private val listener: CropImageListener) : ProcessListener {

    private val mlKitFaceDetection = MLKitFaceDetection(this)
    private val faceDetection = FaceDetection()
    private var capturedImageBitmap: Bitmap? = null

    init {
        initializeMLKit()
    }

    private fun initializeMLKit() {
        mlKitFaceDetection.initFaceDetection()
    }

    fun detectImage(capturedImageBitmap: Bitmap) {
        this.capturedImageBitmap = capturedImageBitmap
        mlKitFaceDetection.detectImage(capturedImageBitmap)
    }

    private fun cropImage(faceFeature: FaceFeature): Bitmap? {
        initOpenCV()
        return faceDetection.detectAndCropFaceFromBitmap(capturedImageBitmap!!, faceFeature)
    }

    private fun initOpenCV() {
        System.loadLibrary("opencv_java4")
        OpenCVLoader.initDebug()
        Mat(3, 3, CvType.CV_8UC1)
    }

    override fun mlKitFaceDetectionListener(faceFeature: FaceFeature) {
        when (faceFeature.isFaceExist) {
            true  -> onExistFaceSuccess(faceFeature)
            false -> onExistFaceFailed("The image does not contain faces, Try again")
        }
    }

    private fun onExistFaceSuccess(faceFeature: FaceFeature) {
        val croppedImage = cropImage(faceFeature)
        if (croppedImage != null) {
            listener.onCroppedSuccess(croppedImage)
        } else {
            onExistFaceFailed("Failed Cropping!")
        }
    }

    private fun onExistFaceFailed(error: String) {
        listener.onCroppedFailed(error)
    }
}

interface ProcessListener {
    fun mlKitFaceDetectionListener(faceFeature: FaceFeature)
}

interface CropImageListener {
    fun onCroppedSuccess(image: Bitmap)
    fun onCroppedFailed(error: String)
}

package com.jetpack.cropimagejetpackcompose.features.open_cv

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.jetpack.cropimagejetpackcompose.features.face_detection.FaceFeature
import org.opencv.core.Mat
import org.opencv.core.Rect
import java.io.ByteArrayOutputStream
import kotlin.math.pow
import kotlin.math.sqrt

private const val TAG = "FaceDetectionTAG"
class FaceDetection {

    fun detectAndCropFaceFromBitmap(
        bitmap: Bitmap,
        faceFeature: FaceFeature
    ): Bitmap? {
        Log.d(TAG, "detectAndCropFaceFromBitmap: start")
        try {
            val imageMat = bitmapToMat(bitmap)
            val faceMat = detectAndCropFaceBasedOnEyes(imageMat, faceFeature)
            return matToBitmap(faceMat)
        } catch (e: Exception) {
            Log.d(TAG, "detectAndCropFaceFromBitmap: Exception= [ " + e.message + " ]")
            return null
        }
    }


    private fun bitmapToMat(bitmap: Bitmap): Mat {
        val mat = Mat()
        val tmpBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        org.opencv.android.Utils.bitmapToMat(tmpBitmap, mat)
        if (mat.empty()) {
            return Mat()
        }
        return mat
    }

    private fun matToBitmap(mat: Mat): Bitmap {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
        org.opencv.android.Utils.matToBitmap(mat, bitmap)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun detectAndCropFaceBasedOnEyes(
        srcMat: Mat,
        faceFeature: FaceFeature
    ): Mat {
        Log.d(TAG, "detectAndCropFaceBasedOnEyes: start")
        val centerX = (faceFeature.LeftEyeX + faceFeature.RightEyeX) / 2
        val centerY = (faceFeature.LeftEyeY + faceFeature.RightEyeY) / 2

        val eyeDistance =
            sqrt((faceFeature.RightEyeX - faceFeature.LeftEyeX).toDouble()
                    .pow(2.0) + (faceFeature.RightEyeY - faceFeature.LeftEyeY).toDouble().pow(2.0)).toFloat()

        val scale = 5.3f
        val margin = (eyeDistance * scale / 2).toInt()

        val xMin = (centerX - margin).coerceAtLeast(0f).toInt()
        val yMin = (centerY - margin).coerceAtLeast(0f).toInt()
        val xMax = (centerX + margin).coerceAtMost(srcMat.cols().toFloat()).toInt()
        val yMax = (centerY + margin).coerceAtMost(srcMat.rows().toFloat()).toInt()

        val roi = Rect(xMin, yMin, xMax - xMin, yMax - yMin)
        val croppedMat = Mat(srcMat, roi)

        return croppedMat
    }

}
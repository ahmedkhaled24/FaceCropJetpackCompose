package com.jetpack.cropimagejetpackcompose.features.face_detection;

import android.graphics.Bitmap;
import android.os.Message;
import android.util.Log;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;
import com.jetpack.cropimagejetpackcompose.viewmodel.ProcessListener;

import java.util.Objects;

public class MLKitFaceDetection {

    ProcessListener listener;

    public MLKitFaceDetection(ProcessListener listener){
        this.listener = listener;
    }

    private static final String TAG  = "MLKitFaceDetectionTAG";
    private FaceDetectorOptions faceDetectorOptions;


    public void initFaceDetection() {
        Log.d(TAG, "initFaceDetection: start");
        int landmarkMode = FaceDetectorOptions.LANDMARK_MODE_ALL;
        int contourMode = FaceDetectorOptions.CONTOUR_MODE_NONE;
        int classificationMode = FaceDetectorOptions.CLASSIFICATION_MODE_ALL;
        int performanceMode = FaceDetectorOptions.PERFORMANCE_MODE_FAST;
        float minFaceSize = 0.7f; // this is to detect the nearest to the camera as 0.0 detecting the faces far away.
        FaceDetectorOptions.Builder optionsBuilder =
                new FaceDetectorOptions.Builder()
                        .setLandmarkMode(landmarkMode)
                        .setContourMode(contourMode)
                        .setClassificationMode(classificationMode)
                        .setPerformanceMode(performanceMode)
                        .setMinFaceSize(minFaceSize);

        faceDetectorOptions = optionsBuilder.build();
        Log.d(TAG, "initFaceDetection: end");
    }

    public void detectImage(Bitmap bitmap) {
        Log.d(TAG, "detectImage: start");
        FaceDetector faceDetector = FaceDetection.getClient(faceDetectorOptions);
        FaceFeature faceFeature = new FaceFeature();

        try {
            InputImage image = InputImage.fromBitmap(bitmap, 0);
            faceDetector.process(image)
                    .addOnSuccessListener(faces -> {
                        Log.d(TAG, "addOnSuccessListener: faces size= " + faces.size());
                        Message message = new Message();
                        if (faces.isEmpty()) {
                            faceFeature.isFaceExist = false;
                        } else {
                            faceFeature.isFaceExist = true;
                            faceFeature.RightEyeX = Objects.requireNonNull(faces.get(0).getLandmark(FaceLandmark.RIGHT_EYE)).getPosition().x;
                            faceFeature.RightEyeY = Objects.requireNonNull(faces.get(0).getLandmark(FaceLandmark.RIGHT_EYE)).getPosition().y;
                            faceFeature.LeftEyeX = Objects.requireNonNull(faces.get(0).getLandmark(FaceLandmark.LEFT_EYE)).getPosition().x;
                            faceFeature.LeftEyeY = Objects.requireNonNull(faces.get(0).getLandmark(FaceLandmark.LEFT_EYE)).getPosition().y;

                            message.obj = faceFeature;
                        }
                        listener.mlKitFaceDetectionListener(faceFeature);
                    })
                    .addOnFailureListener(e -> {
                        Log.d(TAG, "addOnFailureListener: [ " + e.getMessage() + " ]");
                        Message message = new Message();
                        faceFeature.isFaceExist = false;
                        message.obj = faceFeature;
                    });
            Log.d(TAG, "detectImage: end");
        } catch (Exception e) {
            Log.d(TAG, "detectImage Exception: " + e.getMessage());
        }
    }
}

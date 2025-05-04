# CropFaceFromCapturedImageJetpackCompose

An Android application built with **Jetpack Compose**, **CameraX**, **ML Kit**, and **OpenCV** to capture an image, detect the face using eye landmarks, and crop the face area accordingly.

## 📱 Features

- Capture images using **CameraX**
- Detect eye positions using **ML Kit Face Detection**
- Crop the face using **OpenCV** based on eye positions
- Jetpack Compose UI with modern Android architecture

## 🧰 Tech Stack

- **Jetpack Compose** – Declarative UI
- **CameraX** – Camera integration
- **ML Kit** – Eye landmark detection
- **OpenCV** – Face cropping logic
- **Kotlin** – Primary language
- **Coroutines** – Asynchronous tasks

## 🚀 How It Works

1. The app opens the camera using CameraX.
2. The user captures a photo.
3. The image is analyzed using ML Kit to detect facial landmarks, especially eye positions.
4. The coordinates are used to calculate the cropping area.
5. The image is cropped using OpenCV and displayed on the result screen.

## 🛡 Permissions

- **Camera Permission** is requested on launch.
- If denied permanently, the user is informed via Toast and the app does not proceed to camera preview.

## 💡 Notes

- Ensure that OpenCV is initialized correctly in your app.
- CameraX may not function properly on emulators — use a real device.

---

Feel free to contribute or fork the project if you're interested in face detection or image processing on Android!

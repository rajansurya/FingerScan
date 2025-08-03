# FingerScan


# Touchless Fingerprint Scanner App (Android - CameraX + Jetpack Compose)

This is a sample Android app that simulates a touchless 4-finger slap fingerprint capture using a real-time camera feed. The app captures a live image using **CameraX**, segments it into 4 parts, applies simulated quality scoring, encrypts the original image using **AES-256**, and displays the results in the UI.

## ✨ Features

- 📸 Live camera preview with CameraX
- 👆 Simulated 4-finger slap capture
- 🧠 Segments the image into 4 vertical slices (one per finger)
- 🔐 AES-256 encryption of the captured fingerprint image
- 📊 Simulated quality scores
- 🧱 Built with Jetpack Compose, ViewModel, and Coroutines

## 📂 Project Structure

Run the App
Clone this repository.

Open in Android Studio.

Build & Run on a physical device (camera required).

Grant camera permission when prompted.

Tap Capture Fingerprint to begin the process.

🔐 Encryption Info
Symmetric encryption using AES/CBC/PKCS5Padding.

IV is prepended to the encrypted blob.

Encrypted data is shown in Base64 format in the UI.

🧠 Simulated Behavior
Finger segmentation is done by dividing the image width into 4 equal parts.

Quality scores are randomly generated integers between 1 and 5.

📸 Screenshot
(Add a screenshot here once you run it on your device.)

📝 License
This project is for educational/demo purposes only.

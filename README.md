<h1 align="center">
  <img src="https://raw.githubusercontent.com/official-Arvind/CameraTools/main/assets/logo.svg" alt="CameraTools" width="480">
</h1>

<p align="center">
  <strong>Universal Camera Enhancement & Artificial Restriction Bypass for Xiaomi & AOSP</strong><br>
  Engineered specifically for the <strong>Redmi Note 12 Pro 5G / Pro+ 5G / Discovery</strong> (<code>ruby</code> / <code>rubypro</code>) powered by the MediaTek Dimensity 1080 (MT6877V).
</p>

<p align="center">
  <a href="https://github.com/official-Arvind/CameraTools/releases/tag/v1.3.1"><img src="https://img.shields.io/badge/Release-v1.3.1--Pre--Release-blue?style=for-the-badge&logo=github" alt="v1.3.1 Pre-Release"></a>
  <img src="https://img.shields.io/badge/Android-13%20%7C%2014%20(MIUI%20%2F%20HyperOS%20%2F%20AOSP)-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android 13/14">
  <img src="https://img.shields.io/badge/Root-Optional%20(Recommended)-green?style=for-the-badge&logo=superuser" alt="Root Optional">
  <img src="https://img.shields.io/badge/Xposed-API_93%2B%20(LSPosed%20%2F%20Vector)-ff69b4?style=for-the-badge" alt="Xposed API 93+">
  <img src="https://img.shields.io/github/downloads/official-Arvind/CameraTools/total?style=for-the-badge&logo=github&label=Downloads&cacheSeconds=600" alt="Downloads">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-GPL--3.0-orange?style=for-the-badge" alt="GPL-3.0"></a>
</p>

---

## 🌟 Changelog & Release Notes

For detailed release notes, bug fixes, and version history, please see **[CHANGELOG.md](CHANGELOG.md)**.

---

## 🔑 Root Permission (Superuser) — Why It Is Required

> [!NOTE]
> **Please Grant Superuser (Root) Access to CameraTools when prompted by Magisk, KernelSU, or APatch!**

CameraTools uses root access for two critical operations:
1. **Direct Sandbox Preference Sync**: Modern Android (13 & 14) enforces strict SELinux isolation and cross-app private data access restrictions. CameraTools leverages root to copy your configuration directly into the camera application's private sandbox (`/data/data/com.android.camera/shared_prefs/cameratools_prefs.xml`) with world-readable permissions. This ensures 100% reliable preference delivery even when standard XSharedPreferences or IPC ContentProviders are blocked by the OS.
2. **Instant One-Click Camera Restart**: Tapping **"Restart Camera"** in the app uses root (`su -c am force-stop`) to cleanly terminate running camera daemon instances and restart the app, applying your new toggles in seconds without rebooting your phone.

---

## 📸 Supported Camera Applications & Environments

CameraTools automatically detects your ROM environment (`RomDetector`) and injects the appropriate hooks depending on the active package:

### 1. Xiaomi Official & Ported MiuiCamera
* **Package IDs**: `com.android.camera`, `com.miui.camera`
* **Environments**: Stock MIUI 14, HyperOS 1.0/2.0, or ported MiuiCamera modules running on custom AOSP ROMs (LineageOS, crDroid, Evolution X, PixelOS, etc.).
* **Experience**: Full integration including native Leica authentic/vibrant modes, 4K60 video toggles, 50MP normal photo remosaic, Pro Mode 60s shutter, and director mode dual-video.

### 2. Google Camera (GCam) Ports
* **Package IDs**: `com.google.android.GoogleCamera`, `com.google.android.GoogleCameraEng`, `com.android.MGC`, `com.MGC` (BSG), `com.agc.cam` (AGC), `com.shamim.cam` (SGCAM), `org.codeaurora.snapcam`.
* **Experience**: Unlocks 4K 60FPS target FPS ranges (`CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES`), injects a true **150 Mbps bitrate** directly into GCam's `MediaFormat` / `MediaCodec` video pipeline, unlocks manual exposure parameters, and disables system thermal throttling interruptions.

### 3. Standard AOSP Camera2 / LineageOS Aperture / Open Camera
* **Package IDs**: `com.android.camera2`, `org.lineageos.aperture`, `app.grapheneos.camera`, `net.sourceforge.opencamera`.
* **Experience**: Framework-level interception via `CameraCharacteristics` and `MediaRecorder`. Directly exposes 60s exposure time range, 60fps video capture profiles, 150 Mbps recording bitrate, and bypasses thermal restrictions via `PowerManager.getCurrentThermalStatus`.

---

## ⚡ Comprehensive Feature Breakdown

| Feature | Description | Architecture / Implementation |
| :--- | :--- | :--- |
| **🎥 4K 60FPS Video Recording** | Unlocks silky-smooth 4K (3840×2160) recording at 60 frames per second on the rear Sony IMX766 sensor. | Hooks Xiaomi's `ComponentConfigVideoQuality.isSupport60FPS` and quality profile `2108`. In AOSP/GCam, injects `[60, 60]` and `[30, 60]` into `CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES` and verifies `CamcorderProfile.QUALITY_2160P`. |
| **🎬 150 Mbps Cinematic Bitrate** | Vastly elevates video recording fidelity from compressed stock ~30–50 Mbps to a broadcast-grade 150 Mbps stream. | In Xiaomi Camera, replaces compression tables with 150,000,000 bps. In GCam, overrides `MediaFormat` `bitrate` keys. In AOSP, hooks `MediaRecorder.setVideoEncodingBitRate`. |
| **🎞️ Pro RAW (DNG) Native Bayer Capture** | Produces uncompressed, true 12.5MP native Bayer RAW (DNG) digital negatives in Pro Mode with full dynamic range and post-processing flexibility in Lightroom/Photoshop. | Hooks Pro Mode RAW stream flags while ensuring hardware HAL stability. |
| **🔴 Leica Profiles & Watermarks** | Unlocks authentic Leica Authentic & Vibrant color science profiles, custom Leica tuning, and genuine Leica watermarks. | Bypasses Xiaomi's regional Chinese-ROM restriction flags (`isSupportLeicaColor`, `isSupportLeicaFilter`, watermark config). |
| **👥 Dual-Camera Stream (Director Mode 204)** | Enables simultaneous video recording using both the rear main sensor and front selfie camera. | Activates hardware pipeline mode ID `204` with MediaTek Picture-in-Picture (MTK PIP) vendor tags and removes device blacklists. |
| **⏱️ Extended Pro Shutter Range (60s – 1/16000s)** | Extends manual shutter speed up to 60 full seconds for astrophotography and long exposures, and down to 1/16000s for freezing fast action. | Hooks `ComponentManuallyDualLens` and framework `CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE` (62,500ns – 60,000,000,000ns). |
| **❄️ Hardware Thermal Bypass** | Prevents the camera app from throwing thermal popups, blocking 4K60, or forcibly terminating recording when the device heats up. | Neutralizes `ThermalDetector` and spoofer `PowerManager.getCurrentThermalStatus` to `THERMAL_STATUS_NONE (0)`. |
| **🔍 50MP Normal Photo Mode** | Forces standard "Photo" mode to capture full 8192×6144 resolution with seamless MediaTek ISP hardware remosaicing instead of binning down to 12.5MP. | Backend spoofing: Safely exposes ultra-pixel resolution to `com.android.camera2` HAL while keeping normal UI, AE, and HDR controls intact. Strictly restricted to rear primary camera (`Camera 0`) to prevent front camera crashes. |

---

## 📢 Calling AOSP ROM Testers!

Are you running a custom AOSP ROM on your Redmi Note 12 Pro 5G / Pro+ 5G (`ruby` / `rubypro`)? **We want your feedback!**

We have designed CameraTools specifically to support custom ROMs such as **LineageOS**, **crDroid**, **Evolution X**, **PixelOS**, **DerpFest**, and **RisingOS**.

### How to Test on AOSP:

1. **Install Module**:
   * Download and install the latest `v1.3.0` APK from [Releases](https://github.com/official-Arvind/CameraTools/releases).
2. **Grant Superuser (Root) Permissions**:
   * Open the **CameraTools** app. When prompted by Magisk, KernelSU, or APatch, tap **Grant** (Always Allow) so the app can sync settings directly to target camera data directories and execute quick camera restarts.
3. **Enable in LSPosed / Vector**:
   * Open your Xposed manager (LSPosed, Vector, etc.).
   * Enable the **CameraTools** module.
   * **Crucial Step — Verify Module Scope**:
     * For **Ported MiuiCamera**: Ensure `com.android.camera` or `com.miui.camera` is checked in the scope.
     * For **GCam**: Ensure your installed GCam package (e.g. `com.android.MGC`, `com.agc.cam`, `com.shamim.cam`) is checked.
     * For **AOSP Camera**: Ensure `org.lineageos.aperture`, `net.sourceforge.opencamera`, or `com.android.camera2` is checked.
4. **Configure Settings**:
   * Open the **CameraTools** app and toggle the features you want to test.
   * Tap **Restart Camera** inside the app.
5. **Capture & Submit Diagnostic Logs**:
   * Connect your phone to your PC via USB with USB Debugging enabled.
   * Launch your camera app and run the following command in terminal:
     ```bash
     adb logcat -s CameraTools VectorLegacyBridge
     ```
   * If an app crashes, capture the crash stack trace:
     ```bash
     adb logcat -b crash
     ```
6. **Submit Feedback**:
   * Open an issue on our [GitHub Issues Tracker](https://github.com/official-Arvind/CameraTools/issues) with:
     * Your ROM name & Android version (e.g., *crDroid 10.x, Android 14*).
     * Camera application tested & package version.
     * Output from `adb logcat -s CameraTools`.

---

## 🛠️ Installation & Setup Summary

1. **Prerequisites**:
   * Device: Redmi Note 12 Pro 5G / Pro+ 5G / Discovery (`ruby` / `rubypro`)
   * OS: Android 13 or Android 14 (Stock HyperOS / MIUI or AOSP Custom ROM)
   * Root: **Magisk**, **KernelSU**, or **APatch** (Superuser permissions granted to CameraTools)
   * Framework: **LSPosed (Zygisk)** or **Vector** installed and active
2. **Setup Steps**:
   * Install `io.github.official_arvind.cameratools-v1.3.1.apk`.
   * Open **CameraTools** and grant root permission.
   * Activate module in LSPosed / Vector and select camera app(s) in scope.
   * Reboot device or restart zygote.
   * Toggle desired camera enhancements and tap **Restart Camera**.

---

## ⚠️ Disclaimer & Safety

* **Hardware Heat**: Bypassing thermal shutdown limits during extended 4K60 video recording or high ambient temperatures will cause the device to heat up. Use common sense and avoid leaving recording unattended in direct sunlight.
* **Camera Sensor Support**: 50MP RAW and 50MP Normal Photo modes utilize the full sensor layout of the primary Sony IMX766. These modes are strictly bound to Camera ID 0 (rear main lens); the ultra-wide and front cameras will continue to operate at their respective native resolutions without crashing.

---

## 📄 License

This project is licensed under the **GNU General Public License v3.0**. See the [LICENSE](LICENSE) file for full details.

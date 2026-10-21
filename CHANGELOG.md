# 📜 Changelog

All notable changes to the **CameraTools** project are documented in this file.

---

## [v1.3.1] - 2026-10-21 (Pre-Release)

### 🚀 AOSP ROM & Vector / LSPosed Preference Loading Fix
* **LSPosed API 93 Modern XSharedPreferences IPC**:
  * Set `xposedminversion="93"` and `xposedsharedprefs="true"` in `AndroidManifest.xml`.
  * Module UI writes preferences using `Context.MODE_WORLD_READABLE`, safely redirected by LSPosed API 93 into a shared cross-process directory (`/data/misc/.../prefs/io.github.official_arvind.cameratools`).
  * `HookMain` instantiates `new XSharedPreferences("io.github.official_arvind.cameratools", PrefProvider.PREFS_NAME)`, bypassing Android 14 filesystem DAC/MAC sandbox restrictions without touching raw files.
* **Automated SELinux Root Sync Bridge**:
  * Added `syncPrefsWithRoot()` in `MainActivity`: dynamically discovers the target camera application UID (`stat -c %u`), creates the target directories, copies the XML config, sets `chmod 666`, applies `chown`, and runs `restorecon -F` to ensure full SELinux compliance.
  * Ensures 100% reliable preference delivery on rooted AOSP setups (`exists=true canRead=true`).
* **Android 11+ Package Visibility Bypass**:
  * Added `grantCameraUriPermissions()` in `MainActivity` to explicitly grant `FLAG_GRANT_READ_URI_PERMISSION` across all 13 supported camera packages (`com.android.camera`, `com.miui.camera`, GCam packages, Aperture, GrapheneOS Camera, OpenCamera).
* **Multi-Tier Fallback Provider**:
  * Completely revamped `PrefProvider`'s `loadAllBooleans()` to traverse Device-Protected storage (DE), Credential-Protected storage (CE), and fallback XML directly, preventing default `false` returns.
* **Live Lifecycle Preference Reloading**:
  * Added hooks on `Application.onCreate` and `Activity.onResume` in `HookMain` to reload settings immediately when switching between CameraTools and the camera application.
* **Clean Exception Reporting**:
  * Removed swallowed exceptions in `HookMain` ContentProvider queries; all sync errors and hook events are now cleanly logged to logcat via `XposedBridge.log(...)`.

---

## [v1.3.0] - 2026-10-21 (Pre-Release)

### 🌟 Universal AOSP ROM & Multi-Camera Compatibility
* **Universal Android Framework (`android.hardware.camera2` & `android.media`) Hooks**:
  * Added `hookFrameworkLevel`: hooks `CameraCharacteristics.get` for exposure range and 4K60 FPS ranges.
  * Added `PowerManager.getCurrentThermalStatus` hook returning `THERMAL_STATUS_NONE` (0) across all camera apps.
  * Added universal `MediaRecorder.setVideoEncodingBitRate` hook (150 Mbps) for AOSP Camera2 and Open Camera.
  * Added `CamcorderProfile.hasProfile` hook for 4K 60FPS.
* **Multi-Camera Package Scope**:
  * Expanded scope from `com.android.camera` to 13 camera applications including GCam ports (`com.google.android.GoogleCamera`, `MGC`, `AGC`, `Shamim`), `org.lineageos.aperture`, `app.grapheneos.camera`, and `net.sourceforge.opencamera`.
* **Dynamic ROM Environment Detection**:
  * Implemented `RomDetector` to inspect Android system properties (`ro.miui.ui.version.name`, `ro.mi.os.version.name`, `miui.os.Build`) and apply OEM vs Framework hooks conditionally.
* **50MP Normal Photo Fixes**:
  * Replaced stack trace inspection with cleaner caller verification to avoid performance overhead in camera preview loop.
  * Fixed immutable `List` crash when injecting 50MP resolutions into `CameraSize` lists.
* **Modern Material UI Redesign**:
  * Redesigned `MainActivity` layout with dark aesthetic, status indicators, and edge-to-edge window insets.

---

## [v1.0.0] - 2026-09-09

### 🎯 Initial Release for Redmi Note 12 Pro 5G (`ruby` / MT6877)
* **4K 60FPS Video Recording**: Unlocked 60 FPS recording at 3840x2160 resolution on the Sony IMX766 sensor.
* **150 Mbps Cinematic Bitrate**: High-bitrate video capture for 4K60 and 4K30 recording.
* **50MP Bayer RAW Capture**: DNG RAW capture from the native un-binned 50MP sensor layout.
* **Leica Authentic & Vibrant Profiles**: Unlocked Leica color profiles and watermarks.
* **Director Mode (Dual-Camera Stream)**: Concurrent front and rear video recording.
* **Extended Pro Exposure Range**: Manual exposure range from 60 seconds to 1/16000s.
* **Thermal Warning & Shutdown Bypass**: Complete bypass of artificial high-temperature camera shutdown dialogs.

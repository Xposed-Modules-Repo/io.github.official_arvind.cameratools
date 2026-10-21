# Diagnostic Analysis & Architectural Solutions Report: CameraTools Preference Loading on AOSP / Evolution X (Android 14) under Vector / LSPosed

**Author:** Antigravity AI & Diagnostic Research Subagent  
**Date:** October 21, 2026  
**Target:** Redmi Note 12 Pro 5G (`ruby` / MT6877)  
**Environment:** Evolution X (Android 14 AOSP ROM), Vector / LSPosed, `com.android.camera` 5.1.001110.5  

---

## 1. Executive Summary & Problem Breakdown

### The Reported Issue:
Tester Jay installed CameraTools v1.3.0 on Evolution X (Android 14) on Redmi Note 12 Pro 5G (`ruby`) with `com.android.camera` hooked by Vector / LSPosed. The log showed:
```
15:03:50.284 VectorLegacyBridge [CameraTools] Checking prefs file: /data/data/com.android.camera/shared_prefs/cameratools_prefs.xml exists=false canRead=false
15:03:50.284 VectorLegacyBridge [CameraTools] Checking prefs file: /data/data/com.miui.camera/shared_prefs/cameratools_prefs.xml exists=false canRead=false
15:03:50.284 VectorLegacyBridge [CameraTools] Checking prefs file: /data/user_de/0/io.github.official_arvind.cameratools/shared_prefs/io.github.official_arvind.cameratools_preferences.xml exists=false canRead=false
15:03:50.284 VectorLegacyBridge [CameraTools] Checking prefs file: /data/data/io.github.official_arvind.cameratools/shared_prefs/io.github.official_arvind.cameratools_preferences.xml exists=false canRead=false
15:03:50.284 VectorLegacyBridge [CameraTools] Checking prefs file: /data/user_de/0/io.github.official_arvind.cameratools/shared_prefs/camera_tools_prefs.xml exists=false canRead=false
15:03:50.284 VectorLegacyBridge [CameraTools] Checking prefs file: /data/data/io.github.official_arvind.cameratools/shared_prefs/camera_tools_prefs.xml exists=false canRead=false
15:03:50.293 VectorLegacyBridge [CameraTools] Final Preferences applied: 4k60=false, bitrate=false, raw=false, leica=false, dual=false, shutter=false, thermalBypass=false, 50MP=false
```
Meanwhile, on the device's storage, `io.github.official_arvind.cameratools_preferences.xml` within the CameraTools app had all toggles set to `true`.

---

## 2. Core Root Causes Identified

1. **Vector / LSPosed Ran in Legacy Mode (`VectorLegacyBridge`)**:
   - In `AndroidManifest.xml`, `xposedminversion` was set to `54` and `xposedsharedprefs` was missing.
   - LSPosed/Vector treats any module with `xposedminversion < 93` as a legacy Android 5.0 module.
   - Consequently, Vector fell back to `VectorLegacyBridge`, which attempts to read raw files directly from disk—an operation completely blocked on Android 14 by Linux DAC (`0600`) and SELinux MAC isolation.

2. **File-based `XSharedPreferences` Constructor Bypassed LSPosed's IPC**:
   - In `HookMain.java`, the code executed `new XSharedPreferences(deFile)`.
   - LSPosed documentation explicitly notes that passing a `File` object forces direct Linux file access and bypasses the framework bridge. It must be initialized strictly as `new XSharedPreferences(packageName, prefFileName)`.

3. **`MainActivity` Saved with `MODE_PRIVATE` Instead of `MODE_WORLD_READABLE`**:
   - In `MainActivity.java`, preferences were stored using standard `MODE_PRIVATE`.
   - When `xposedsharedprefs="true"` (or `xposedminversion >= 93`) is set, LSPosed hooks `ContextImpl.checkMode()` and `getPreferencesDir()` specifically so modules can use `Context.MODE_WORLD_READABLE` to safely expose settings across the sandbox.

4. **Android 11+ Package Visibility Filter Blocked ContentProvider IPC**:
   - On Android 11+ (API 30+), `system_server` strictly filters cross-app component queries.
   - Because `com.android.camera` is an external OEM application that does not declare `<queries><provider android:authorities="io.github.official_arvind.cameratools.prefs" /></queries>`, `ContentResolver.call` throws `IllegalArgumentException: Unknown authority`.
   - The ContentProvider call in `HookMain.java` was wrapped in `catch (Throwable cpErr) { ... }`, silently swallowing the exception.

5. **Fragile and Unchecked Background `su` Shell Execution in `MainActivity.java`**:
   - The root copy command ran asynchronously via `su -c <chained_&&_command>` with zero error checking, no root availability checks, hardcoded single-user paths, missing SELinux `restorecon`, and missing camera UID `chown`.
   - If root authorization was ungranted or timed out, it failed silently without creating `/data/data/com.android.camera/shared_prefs/cameratools_prefs.xml`.

---

## 3. Four-Tier Architecture for v1.3.1

```
┌────────────────────────────────────────────────────────────────────────┐
│                   Tier 1: Modern LSPosed Remote Prefs                  │
│  • xposedminversion="93" + xposedsharedprefs="true"                    │
│  • MODE_WORLD_READABLE redirected by LSPosed to /data/misc/...         │
│  • Non-Rooted + Rooted: 100% works out-of-the-box in Vector/LSPosed    │
└────────────────────────────────────────────────────────────────────────┘
                                    │ (Fallback if bridge inactive)
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                   Tier 2: Direct XML & Robust Root Sync                │
│  • MainActivity checks root status and prompts if available            │
│  • Resolves actual camera UID + chown <uid>:<gid> + restorecon         │
│  • Copies to target app shared_prefs with proper SELinux context       │
│  • Explicit user-facing "Sync Preferences (Root)" action               │
└────────────────────────────────────────────────────────────────────────┘
                                    │ (Fallback if app is launching)
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                   Tier 3: Cross-Process URI Grant IPC                  │
│  • MainActivity grants URI permission to camera packages               │
│  • Bypasses Android 11+ Package Visibility filter                      │
│  • Hook in Application.onCreate() executes ContentResolver.call()      │
└────────────────────────────────────────────────────────────────────────┘
                                    │ (Fallback)
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                   Tier 4: Dynamic Lifecycle Reloading                  │
│  • Preference reloading hooked on Application.onCreate & resume        │
│  • Detailed Logcat reporting at all stages (no swallowed errors)       │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Implementation Plan for v1.3.1

1. **`AndroidManifest.xml`**:
   - Bump `versionCode="131"`, `versionName="1.3.1"`.
   - Set `xposedminversion="93"`.
   - Add `<meta-data android:name="xposedsharedprefs" android:value="true" />`.
   - Add `<uses-permission android:name="android.permission.ACCESS_SUPERUSER" />`.

2. **`MainActivity.java`**:
   - Initialize `SharedPreferences` with `Context.MODE_WORLD_READABLE`, with fallback to `Context.MODE_PRIVATE`.
   - Add `grantUriPermission()` for `com.android.camera` and `com.miui.camera`.
   - Add robust root sync logic (`syncPrefsViaRoot`) with camera UID discovery (`stat -c %u`), `chmod 666`, `chown`, and `restorecon -F`.
   - Add a "Sync Settings to Camera" button in the dock with a toast confirming success/failure.

3. **`HookMain.java`**:
   - Replace `XSharedPreferences(File)` with `new XSharedPreferences("io.github.official_arvind.cameratools", PrefProvider.PREFS_NAME)`.
   - Hook `android.app.Application.class, "onCreate"` in addition to `attachBaseContext`.
   - Log all ContentProvider errors instead of swallowing them.

4. **Build & Release**:
   - Compile v1.3.1 using `build_apk.ps1`.
   - Verify on local connected device.
   - Tag and release v1.3.1 on GitHub.

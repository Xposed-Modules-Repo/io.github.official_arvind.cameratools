# Keep Xposed module entry point
-keep class io.github.official_arvind.cameratools.HookMain { *; }
-keep class io.github.official_arvind.cameratools.PrefProvider { *; }
-keep class io.github.official_arvind.cameratools.RomDetector { *; }
-keep class io.github.official_arvind.cameratools.MainActivity { *; }

# Keep all Xposed API classes
-keep class de.robv.android.xposed.** { *; }

# Keep Android framework classes referenced by hooks
-keep class android.hardware.camera2.** { *; }
-keep class android.media.** { *; }
-keep class android.os.PowerManager { *; }

# Don't warn about Xposed stubs
-dontwarn de.robv.android.xposed.**

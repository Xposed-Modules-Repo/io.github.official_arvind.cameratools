package io.github.official_arvind.cameratools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.util.Map;

public class MainActivity extends Activity {
    private SharedPreferences prefs;

    private Switch sw4k60;
    private Switch swBitrate;
    private Switch swRaw;
    private Switch swLeica;
    private Switch swDualVideo;
    private Switch swShutter;
    private Switch swThermal;
    private Switch swHighResPhoto;
    private Switch swLogFormat;
    private Switch swMiMovie;
    private Switch swPrompter;
    private Switch swFocusPeak;
    private Switch swLeicaLenses;
    private Switch swDocMode;
    private Switch swAudioMic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            window.setNavigationBarContrastEnforced(false);
        }
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            window.setDecorFitsSystemWindows(false);
        }

        setContentView(R.layout.activity_main);
        
        final View bottomBar = findViewById(R.id.bottom_bar);
        final View scrollView = findViewById(R.id.scroll_view);
        bottomBar.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                int bottom = insets.getSystemWindowInsetBottom();
                int dp16 = (int)(16 * getResources().getDisplayMetrics().density);
                
                android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) v.getLayoutParams();
                params.bottomMargin = dp16 + bottom;
                v.setLayoutParams(params);
                
                int top = insets.getSystemWindowInsetTop();
                int dp20 = (int)(20 * getResources().getDisplayMetrics().density);
                int dp24 = (int)(24 * getResources().getDisplayMetrics().density);
                
                int bottomBarTotalHeight = (int)(96 * getResources().getDisplayMetrics().density) + bottom;
                scrollView.setPadding(dp16, dp20 + top, dp16, dp24 + bottomBarTotalHeight);
                
                return insets;
            }
        });

        // Initialize SharedPreferences - under LSPosed API 93+, MODE_WORLD_READABLE is redirected
        // to a shared directory accessible by hooked processes.
        Context deCtx = isDeviceProtectedStorage() ? this : createDeviceProtectedStorageContext();
        try {
            prefs = deCtx.getSharedPreferences(PrefProvider.PREFS_NAME, Context.MODE_WORLD_READABLE);
        } catch (Throwable e) {
            prefs = deCtx.getSharedPreferences(PrefProvider.PREFS_NAME, Context.MODE_PRIVATE);
        }

        sw4k60 = findViewById(R.id.switch_4k60);
        swBitrate = findViewById(R.id.switch_bitrate);
        swRaw = findViewById(R.id.switch_raw);
        swLeica = findViewById(R.id.switch_leica);
        swDualVideo = findViewById(R.id.switch_dual_video);
        swShutter = findViewById(R.id.switch_shutter);
        swThermal = findViewById(R.id.switch_thermal);
        swHighResPhoto = findViewById(R.id.switch_high_res_photo);
        swLogFormat = findViewById(R.id.switch_log_format);
        swMiMovie = findViewById(R.id.switch_mimovie);
        swPrompter = findViewById(R.id.switch_prompter);
        swFocusPeak = findViewById(R.id.switch_focus_peak);
        swLeicaLenses = findViewById(R.id.switch_leica_lenses);
        swDocMode = findViewById(R.id.switch_doc_mode);
        swAudioMic = findViewById(R.id.switch_audio_mic);

        // Ensure defaults are saved if any key is missing
        SharedPreferences.Editor edInit = prefs.edit();
        boolean changed = false;
        if (!prefs.contains(PrefProvider.KEY_4K60)) { edInit.putBoolean(PrefProvider.KEY_4K60, false); changed = true; }
        if (!prefs.contains(PrefProvider.KEY_BITRATE)) { edInit.putBoolean(PrefProvider.KEY_BITRATE, false); changed = true; }
        if (!prefs.contains(PrefProvider.KEY_RAW)) { edInit.putBoolean(PrefProvider.KEY_RAW, false); changed = true; }
        if (!prefs.contains(PrefProvider.KEY_LEICA)) { edInit.putBoolean(PrefProvider.KEY_LEICA, false); changed = true; }
        if (!prefs.contains(PrefProvider.KEY_DUAL_VIDEO)) { edInit.putBoolean(PrefProvider.KEY_DUAL_VIDEO, false); changed = true; }
        if (!prefs.contains(PrefProvider.KEY_SHUTTER)) { edInit.putBoolean(PrefProvider.KEY_SHUTTER, false); changed = true; }
        if (!prefs.contains(PrefProvider.KEY_DISABLE_THERMAL)) { edInit.putBoolean(PrefProvider.KEY_DISABLE_THERMAL, false); changed = true; }
        if (!prefs.contains(PrefProvider.KEY_HIGH_RES_PHOTO)) { edInit.putBoolean(PrefProvider.KEY_HIGH_RES_PHOTO, false); changed = true; }
        if (!prefs.contains(PrefProvider.KEY_LOG_FORMAT)) { edInit.putBoolean(PrefProvider.KEY_LOG_FORMAT, false); changed = true; }
        if (!prefs.contains(PrefProvider.KEY_MIMOVIE)) { edInit.putBoolean(PrefProvider.KEY_MIMOVIE, false); changed = true; }
        if (!prefs.contains(PrefProvider.KEY_PROMPTER)) { edInit.putBoolean(PrefProvider.KEY_PROMPTER, false); changed = true; }
        if (!prefs.contains(PrefProvider.KEY_FOCUS_PEAK)) { edInit.putBoolean(PrefProvider.KEY_FOCUS_PEAK, false); changed = true; }
        if (!prefs.contains(PrefProvider.KEY_LEICA_LENSES)) { edInit.putBoolean(PrefProvider.KEY_LEICA_LENSES, false); changed = true; }
        if (!prefs.contains(PrefProvider.KEY_TRACK_FOCUS)) { edInit.putBoolean(PrefProvider.KEY_TRACK_FOCUS, false); changed = true; }
        if (!prefs.contains(PrefProvider.KEY_DOC_MODE)) { edInit.putBoolean(PrefProvider.KEY_DOC_MODE, false); changed = true; }
        if (!prefs.contains(PrefProvider.KEY_AUDIO_MIC)) { edInit.putBoolean(PrefProvider.KEY_AUDIO_MIC, false); changed = true; }
        if (changed) {
            edInit.commit();
        }

        makePrefsWorldReadable();

        loadPreferences();
        setupListeners();

        final Button btnRestart = findViewById(R.id.btn_restart);
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                btnRestart.setEnabled(false);
                btnRestart.setText("Restarting...");
                restartCamera(btnRestart);
            }
        });

        final Button btnEnableAll = findViewById(R.id.btn_enable_all);
        updateToggleAllButtonText(btnEnableAll);
        btnEnableAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
                boolean allActive = areAllEnabled();
                setAll(!allActive);
                updateToggleAllButtonText(btnEnableAll);
            }
        });
    }

    private boolean areAllEnabled() {
        return sw4k60.isChecked() && swBitrate.isChecked() && swRaw.isChecked()
                && swLeica.isChecked() && swDualVideo.isChecked() && swShutter.isChecked()
                && swThermal.isChecked() && swHighResPhoto.isChecked() && swLogFormat.isChecked()
                && swMiMovie.isChecked() && swPrompter.isChecked() && swFocusPeak.isChecked()
                && swLeicaLenses.isChecked() && swDocMode.isChecked() && swAudioMic.isChecked();
    }

    private void updateToggleAllButtonText(Button btn) {
        if (btn != null) {
            btn.setText(areAllEnabled() ? "Disable All" : "Enable All");
        }
    }

    private void loadPreferences() {
        sw4k60.setChecked(prefs.getBoolean(PrefProvider.KEY_4K60, false));
        swBitrate.setChecked(prefs.getBoolean(PrefProvider.KEY_BITRATE, false));
        swRaw.setChecked(prefs.getBoolean(PrefProvider.KEY_RAW, false));
        swLeica.setChecked(prefs.getBoolean(PrefProvider.KEY_LEICA, false));
        swDualVideo.setChecked(prefs.getBoolean(PrefProvider.KEY_DUAL_VIDEO, false));
        swShutter.setChecked(prefs.getBoolean(PrefProvider.KEY_SHUTTER, false));
        swThermal.setChecked(prefs.getBoolean(PrefProvider.KEY_DISABLE_THERMAL, false));
        swHighResPhoto.setChecked(prefs.getBoolean(PrefProvider.KEY_HIGH_RES_PHOTO, false));
        swLogFormat.setChecked(prefs.getBoolean(PrefProvider.KEY_LOG_FORMAT, false));
        swMiMovie.setChecked(prefs.getBoolean(PrefProvider.KEY_MIMOVIE, false));
        swPrompter.setChecked(prefs.getBoolean(PrefProvider.KEY_PROMPTER, false));
        swFocusPeak.setChecked(prefs.getBoolean(PrefProvider.KEY_FOCUS_PEAK, false));
        swLeicaLenses.setChecked(prefs.getBoolean(PrefProvider.KEY_TRACK_FOCUS, prefs.getBoolean(PrefProvider.KEY_LEICA_LENSES, false)));
        swDocMode.setChecked(prefs.getBoolean(PrefProvider.KEY_DOC_MODE, false));
        swAudioMic.setChecked(prefs.getBoolean(PrefProvider.KEY_AUDIO_MIC, false));
    }

    private void setupListeners() {
        final Button btnEnableAll = findViewById(R.id.btn_enable_all);
        CompoundButtonListener listener = new CompoundButtonListener(btnEnableAll);
        sw4k60.setOnCheckedChangeListener(listener);
        swBitrate.setOnCheckedChangeListener(listener);
        swRaw.setOnCheckedChangeListener(listener);
        swLeica.setOnCheckedChangeListener(listener);
        swDualVideo.setOnCheckedChangeListener(listener);
        swShutter.setOnCheckedChangeListener(listener);
        swThermal.setOnCheckedChangeListener(listener);
        swHighResPhoto.setOnCheckedChangeListener(listener);
        swLogFormat.setOnCheckedChangeListener(listener);
        swMiMovie.setOnCheckedChangeListener(listener);
        swPrompter.setOnCheckedChangeListener(listener);
        swFocusPeak.setOnCheckedChangeListener(listener);
        swLeicaLenses.setOnCheckedChangeListener(listener);
        swDocMode.setOnCheckedChangeListener(listener);
        swAudioMic.setOnCheckedChangeListener(listener);
    }

    private class CompoundButtonListener implements android.widget.CompoundButton.OnCheckedChangeListener {
        private final Button btnToggle;

        public CompoundButtonListener(Button btnToggle) {
            this.btnToggle = btnToggle;
        }

        @Override
        public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
            buttonView.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
            SharedPreferences.Editor ed = prefs.edit();
            int id = buttonView.getId();
            if (id == R.id.switch_4k60) ed.putBoolean(PrefProvider.KEY_4K60, isChecked);
            else if (id == R.id.switch_bitrate) ed.putBoolean(PrefProvider.KEY_BITRATE, isChecked);
            else if (id == R.id.switch_raw) ed.putBoolean(PrefProvider.KEY_RAW, isChecked);
            else if (id == R.id.switch_leica) ed.putBoolean(PrefProvider.KEY_LEICA, isChecked);
            else if (id == R.id.switch_dual_video) ed.putBoolean(PrefProvider.KEY_DUAL_VIDEO, isChecked);
            else if (id == R.id.switch_shutter) ed.putBoolean(PrefProvider.KEY_SHUTTER, isChecked);
            else if (id == R.id.switch_thermal) ed.putBoolean(PrefProvider.KEY_DISABLE_THERMAL, isChecked);
            else if (id == R.id.switch_high_res_photo) ed.putBoolean(PrefProvider.KEY_HIGH_RES_PHOTO, isChecked);
            else if (id == R.id.switch_log_format) ed.putBoolean(PrefProvider.KEY_LOG_FORMAT, isChecked);
            else if (id == R.id.switch_mimovie) ed.putBoolean(PrefProvider.KEY_MIMOVIE, isChecked);
            else if (id == R.id.switch_prompter) ed.putBoolean(PrefProvider.KEY_PROMPTER, isChecked);
            else if (id == R.id.switch_focus_peak) ed.putBoolean(PrefProvider.KEY_FOCUS_PEAK, isChecked);
            else if (id == R.id.switch_leica_lenses) {
                ed.putBoolean(PrefProvider.KEY_TRACK_FOCUS, isChecked);
                ed.putBoolean(PrefProvider.KEY_LEICA_LENSES, isChecked);
            }
            else if (id == R.id.switch_doc_mode) ed.putBoolean(PrefProvider.KEY_DOC_MODE, isChecked);
            else if (id == R.id.switch_audio_mic) ed.putBoolean(PrefProvider.KEY_AUDIO_MIC, isChecked);
            ed.commit();
            makePrefsWorldReadable();
            updateToggleAllButtonText(btnToggle);
            Toast.makeText(MainActivity.this, isChecked ? "Feature enabled - Restart Camera to apply" : "Feature disabled - Restart Camera to apply", Toast.LENGTH_SHORT).show();
        }
    }

    private void grantCameraUriPermissions() {
        try {
            Uri uri = Uri.parse("content://" + PrefProvider.AUTHORITY);
            String[] targetPkgs = new String[]{
                "com.android.camera",
                "com.miui.camera",
                "com.google.android.GoogleCamera",
                "com.google.android.GoogleCameraEng",
                "com.android.MGC",
                "com.MGC",
                "com.agc.cam",
                "com.shamim.cam",
                "org.codeaurora.snapcam",
                "com.android.camera2",
                "org.lineageos.aperture",
                "app.grapheneos.camera",
                "net.sourceforge.opencamera"
            };
            for (String pkg : targetPkgs) {
                try {
                    grantUriPermission(pkg, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
    }

    private static String getSuPath() {
        String[] locations = {
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/data/adb/ksu/bin/su",
            "/data/adb/ap/bin/su"
        };
        for (String loc : locations) {
            File f = new File(loc);
            if (f.exists()) {
                return loc;
            }
        }
        return "/system/bin/su";
    }

    private void syncPrefsWithRoot(final boolean showToast) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = false;
                try {
                    Context deCtx = isDeviceProtectedStorage() ? MainActivity.this : createDeviceProtectedStorageContext();
                    File deDir = deCtx.getDataDir();
                    File srcFile = new File(deDir, "shared_prefs/" + PrefProvider.PREFS_NAME + ".xml");
                    if (!srcFile.exists() && getFilesDir() != null && getFilesDir().getParentFile() != null) {
                        srcFile = new File(getFilesDir().getParentFile(), "shared_prefs/" + PrefProvider.PREFS_NAME + ".xml");
                    }
                    if (srcFile.exists()) {
                        String srcPath = srcFile.getAbsolutePath();
                        String script =
                            "for PKG in com.android.camera com.miui.camera; do\n" +
                            "  TARGET_DIR=\"/data/data/$PKG/shared_prefs\"\n" +
                            "  if [ ! -d \"/data/data/$PKG\" ] && [ -d \"/data/user/0/$PKG\" ]; then\n" +
                            "    TARGET_DIR=\"/data/user/0/$PKG/shared_prefs\"\n" +
                            "  fi\n" +
                            "  PARENT=\"$(dirname \"$TARGET_DIR\")\"\n" +
                            "  if [ -d \"$PARENT\" ]; then\n" +
                            "    mkdir -p \"$TARGET_DIR\"\n" +
                            "    chmod 777 \"$TARGET_DIR\"\n" +
                            "    TARGET_FILE=\"$TARGET_DIR/cameratools_prefs.xml\"\n" +
                            "    cp \"" + srcPath + "\" \"$TARGET_FILE\"\n" +
                            "    chmod 666 \"$TARGET_FILE\"\n" +
                            "    PKG_UID=$(stat -c '%u' \"$PARENT\" 2>/dev/null || echo 0)\n" +
                            "    if [ \"$PKG_UID\" != \"0\" ]; then\n" +
                            "      chown -R ${PKG_UID}:${PKG_UID} \"$TARGET_DIR\" 2>/dev/null || true\n" +
                            "    fi\n" +
                            "    restorecon -F \"$TARGET_FILE\" 2>/dev/null || true\n" +
                            "    restorecon -R \"$TARGET_DIR\" 2>/dev/null || true\n" +
                            "  fi\n" +
                            "done\n";

                        Process p = Runtime.getRuntime().exec(new String[]{getSuPath(), "-c", script});
                        try { p.getInputStream().close(); } catch (Throwable ignored) {}
                        try { p.getErrorStream().close(); } catch (Throwable ignored) {}
                        int exitCode = p.waitFor();
                        success = (exitCode == 0);
                    }
                } catch (Throwable t) {
                    success = false;
                }

                if (showToast) {
                    final boolean finalSuccess = success;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (finalSuccess) {
                                Toast.makeText(MainActivity.this, "Preferences synced to Camera storage via Root!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Root sync skipped or not granted (LSPosed IPC will handle preferences)", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void makePrefsWorldReadable() {
        try {
            Context deCtx = isDeviceProtectedStorage() ? this : createDeviceProtectedStorageContext();
            File deDir = deCtx.getDataDir();
            if (deDir != null) {
                deDir.setReadable(true, false);
                deDir.setExecutable(true, false);
                File spDir = new File(deDir, "shared_prefs");
                if (spDir.exists()) {
                    spDir.setReadable(true, false);
                    spDir.setExecutable(true, false);
                    File f = new File(spDir, PrefProvider.PREFS_NAME + ".xml");
                    if (f.exists()) f.setReadable(true, false);
                }
            }

            // Also mirror to standard private storage
            SharedPreferences normalSp = getSharedPreferences(PrefProvider.PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor normalEd = normalSp.edit();
            for (Map.Entry<String, ?> entry : prefs.getAll().entrySet()) {
                if (entry.getValue() instanceof Boolean) {
                    normalEd.putBoolean(entry.getKey(), (Boolean) entry.getValue());
                }
            }
            normalEd.commit();

            File normalFiles = getFilesDir();
            if (normalFiles != null) {
                File normalDir = normalFiles.getParentFile();
                if (normalDir != null) {
                    normalDir.setReadable(true, false);
                    normalDir.setExecutable(true, false);
                    File normalSpDir = new File(normalDir, "shared_prefs");
                    if (normalSpDir.exists()) {
                        normalSpDir.setReadable(true, false);
                        normalSpDir.setExecutable(true, false);
                        File f = new File(normalSpDir, PrefProvider.PREFS_NAME + ".xml");
                        if (f.exists()) f.setReadable(true, false);
                    }
                }
            }

            // Bypass Android 11+ Package Visibility for ContentProvider
            grantCameraUriPermissions();

            // Background Root sync
            syncPrefsWithRoot(false);
        } catch (Throwable ignored) {}
    }

    private void setAll(boolean state) {
        sw4k60.setChecked(state);
        swBitrate.setChecked(state);
        swRaw.setChecked(state);
        swLeica.setChecked(state);
        swDualVideo.setChecked(state);
        swShutter.setChecked(state);
        swThermal.setChecked(state);
        swHighResPhoto.setChecked(state);
        swLogFormat.setChecked(state);
        swMiMovie.setChecked(state);
        swPrompter.setChecked(state);
        swFocusPeak.setChecked(state);
        swLeicaLenses.setChecked(state);
        swDocMode.setChecked(state);
        swAudioMic.setChecked(state);
        prefs.edit()
            .putBoolean(PrefProvider.KEY_4K60, state)
            .putBoolean(PrefProvider.KEY_BITRATE, state)
            .putBoolean(PrefProvider.KEY_RAW, state)
            .putBoolean(PrefProvider.KEY_LEICA, state)
            .putBoolean(PrefProvider.KEY_DUAL_VIDEO, state)
            .putBoolean(PrefProvider.KEY_SHUTTER, state)
            .putBoolean(PrefProvider.KEY_DISABLE_THERMAL, state)
            .putBoolean(PrefProvider.KEY_HIGH_RES_PHOTO, state)
            .putBoolean(PrefProvider.KEY_LOG_FORMAT, state)
            .putBoolean(PrefProvider.KEY_MIMOVIE, state)
            .putBoolean(PrefProvider.KEY_PROMPTER, state)
            .putBoolean(PrefProvider.KEY_FOCUS_PEAK, state)
            .putBoolean(PrefProvider.KEY_LEICA_LENSES, state)
            .putBoolean(PrefProvider.KEY_TRACK_FOCUS, state)
            .putBoolean(PrefProvider.KEY_DOC_MODE, state)
            .putBoolean(PrefProvider.KEY_AUDIO_MIC, state)
            .commit();
        makePrefsWorldReadable();
        Toast.makeText(this, state ? "All features activated" : "All features deactivated", Toast.LENGTH_SHORT).show();
    }

    private void restartCamera(final Button btnRestart) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                syncPrefsWithRoot(false);
                try {
                    Process p = Runtime.getRuntime().exec(new String[]{getSuPath(), "-c", "am force-stop com.android.camera; am force-stop com.miui.camera"});
                    try { p.getInputStream().close(); } catch (Throwable ignored) {}
                    try { p.getErrorStream().close(); } catch (Throwable ignored) {}
                    p.waitFor();
                } catch (Throwable t) {
                    try {
                        android.app.ActivityManager am = (android.app.ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                        am.killBackgroundProcesses("com.android.camera");
                        am.killBackgroundProcesses("com.miui.camera");
                    } catch (Exception ignored) {}
                }
                
                try { Thread.sleep(600); } catch (InterruptedException ignored) {}
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (btnRestart != null) {
                            btnRestart.setEnabled(true);
                            btnRestart.setText(R.string.btn_restart_camera);
                        }
                        try {
                            Intent intent = getPackageManager().getLaunchIntentForPackage("com.android.camera");
                            if (intent == null) {
                                intent = getPackageManager().getLaunchIntentForPackage("com.miui.camera");
                            }
                            if (intent == null) {
                                intent = new Intent(android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                            }
                            if (intent != null) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Toast.makeText(MainActivity.this, "Camera restarted!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Please open Camera manually.", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Please force stop Camera manually.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }
}

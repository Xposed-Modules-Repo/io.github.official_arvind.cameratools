package io.github.official_arvind.cameratools;

import java.lang.reflect.Method;

public class RomDetector {
    private static Boolean sIsMiui = null;

    public static boolean isMiuiOrHyperOs() {
        if (sIsMiui != null) return sIsMiui;
        try {
            Class<?> sp = Class.forName("android.os.SystemProperties");
            Method get = sp.getMethod("get", String.class, String.class);
            String miuiVer = (String) get.invoke(null, "ro.miui.ui.version.name", "");
            if (miuiVer != null && !miuiVer.trim().isEmpty()) { sIsMiui = true; return true; }
            String hyperVer = (String) get.invoke(null, "ro.mi.os.version.name", "");
            if (hyperVer != null && !hyperVer.trim().isEmpty()) { sIsMiui = true; return true; }
            Class.forName("miui.os.Build");
            sIsMiui = true; return true;
        } catch (Throwable t) {
            sIsMiui = false; return false;
        }
    }
}

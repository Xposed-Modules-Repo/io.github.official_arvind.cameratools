package io.github.official_arvind.cameratools;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;

public class PrefProvider extends ContentProvider {
    public static final String AUTHORITY = "io.github.official_arvind.cameratools.prefs";
    public static final String PREFS_NAME = "io.github.official_arvind.cameratools_preferences";

    public static final String KEY_4K60 = "pref_4k60";
    public static final String KEY_BITRATE = "pref_bitrate";
    public static final String KEY_RAW = "pref_raw";
    public static final String KEY_LEICA = "pref_leica";
    public static final String KEY_DUAL_VIDEO = "pref_dual_video";
    public static final String KEY_SHUTTER = "pref_shutter";
    public static final String KEY_DISABLE_THERMAL = "pref_disable_thermal";
    public static final String KEY_HIGH_RES_PHOTO = "pref_high_res_photo";

    @Override
    public boolean onCreate() {
        return true;
    }

    private Map<String, Boolean> loadAllBooleans(Context ctx) {
        Map<String, Boolean> map = new HashMap<>();

        // 1. Try DE storage with MODE_WORLD_READABLE (LSPosed shared) then MODE_PRIVATE
        try {
            Context deCtx = ctx.isDeviceProtectedStorage() ? ctx : ctx.createDeviceProtectedStorageContext();
            SharedPreferences sp = null;
            try {
                sp = deCtx.getSharedPreferences(PREFS_NAME, Context.MODE_WORLD_READABLE);
            } catch (Throwable t) {
                sp = deCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            }
            if (sp != null && !sp.getAll().isEmpty()) {
                for (Map.Entry<String, ?> e : sp.getAll().entrySet()) {
                    if (e.getValue() instanceof Boolean) {
                        map.put(e.getKey(), (Boolean) e.getValue());
                    }
                }
            }
        } catch (Throwable ignored) {}

        // 2. Try standard CE storage
        if (map.isEmpty()) {
            try {
                SharedPreferences ceSp = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                if (ceSp != null && !ceSp.getAll().isEmpty()) {
                    for (Map.Entry<String, ?> e : ceSp.getAll().entrySet()) {
                        if (e.getValue() instanceof Boolean) {
                            map.put(e.getKey(), (Boolean) e.getValue());
                        }
                    }
                }
            } catch (Throwable ignored) {}
        }

        // 3. Fallback: Directly parse XML from storage
        if (map.isEmpty()) {
            File[] files = new File[]{
                new File("/data/user_de/0/io.github.official_arvind.cameratools/shared_prefs/" + PREFS_NAME + ".xml"),
                new File("/data/data/io.github.official_arvind.cameratools/shared_prefs/" + PREFS_NAME + ".xml")
            };
            for (File f : files) {
                if (f.exists() && f.canRead()) {
                    try (FileInputStream fis = new FileInputStream(f)) {
                        XmlPullParser parser = Xml.newPullParser();
                        parser.setInput(fis, "utf-8");
                        int eventType = parser.getEventType();
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            if (eventType == XmlPullParser.START_TAG && "boolean".equals(parser.getName())) {
                                String name = parser.getAttributeValue(null, "name");
                                boolean val = Boolean.parseBoolean(parser.getAttributeValue(null, "value"));
                                if (name != null) map.put(name, val);
                            }
                            eventType = parser.next();
                        }
                        if (!map.isEmpty()) break;
                    } catch (Throwable ignored) {}
                }
            }
        }

        return map;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        Context ctx = getContext();
        if (ctx == null) return null;

        Map<String, Boolean> map = loadAllBooleans(ctx);

        if ("get_all".equals(method)) {
            Bundle b = new Bundle();
            b.putBoolean(KEY_4K60, map.containsKey(KEY_4K60) ? map.get(KEY_4K60) : false);
            b.putBoolean(KEY_BITRATE, map.containsKey(KEY_BITRATE) ? map.get(KEY_BITRATE) : false);
            b.putBoolean(KEY_RAW, map.containsKey(KEY_RAW) ? map.get(KEY_RAW) : false);
            b.putBoolean(KEY_LEICA, map.containsKey(KEY_LEICA) ? map.get(KEY_LEICA) : false);
            b.putBoolean(KEY_DUAL_VIDEO, map.containsKey(KEY_DUAL_VIDEO) ? map.get(KEY_DUAL_VIDEO) : false);
            b.putBoolean(KEY_SHUTTER, map.containsKey(KEY_SHUTTER) ? map.get(KEY_SHUTTER) : false);
            b.putBoolean(KEY_DISABLE_THERMAL, map.containsKey(KEY_DISABLE_THERMAL) ? map.get(KEY_DISABLE_THERMAL) : false);
            b.putBoolean(KEY_HIGH_RES_PHOTO, map.containsKey(KEY_HIGH_RES_PHOTO) ? map.get(KEY_HIGH_RES_PHOTO) : false);
            return b;
        } else if ("get_boolean".equals(method)) {
            if (arg == null) return null;
            Bundle b = new Bundle();
            b.putBoolean("value", map.containsKey(arg) ? map.get(arg) : false);
            return b;
        }
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/vnd.io.github.official_arvind.cameratools.pref";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

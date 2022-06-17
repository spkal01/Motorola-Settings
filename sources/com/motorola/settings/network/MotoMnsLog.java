package com.motorola.settings.network;

import android.os.SystemProperties;
import android.util.Log;

public class MotoMnsLog {
    public static final boolean DEBUG = isLoggable(3);
    public static final boolean ERROR = isLoggable(6);
    public static final boolean FORCE_LOGGING;
    public static final boolean INFO = isLoggable(4);
    public static final boolean VERBOSE = isLoggable(2);
    public static final boolean WARN = isLoggable(5);

    static {
        boolean z = false;
        if (SystemProperties.getInt("ro.debuggable", 0) == 1) {
            z = true;
        }
        FORCE_LOGGING = z;
    }

    public static boolean isLoggable(int i) {
        return FORCE_LOGGING || Log.isLoggable("MotoMnsLog", i);
    }

    public static void logd(String str, String str2) {
        if (DEBUG) {
            Log.d("MotoMnsLog", "[" + str + "] " + str2);
        }
    }

    public static void logi(String str, String str2) {
        if (INFO) {
            Log.i("MotoMnsLog", "[" + str + "] " + str2);
        }
    }

    public static void logw(String str, String str2) {
        if (WARN) {
            Log.w("MotoMnsLog", "[" + str + "] " + str2);
        }
    }

    public static void loge(String str, String str2) {
        if (ERROR) {
            Log.e("MotoMnsLog", "[" + str + "] " + str2);
        }
    }
}

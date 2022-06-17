package com.motorola.settings.resetsettings;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceSettingsUtils {
    public static final boolean DBG = Build.IS_DEBUGGABLE;
    private static final String TAG = "DeviceSettingsUtils";

    public static int getIntegerResources(String str, String str2, Context context, int i) {
        try {
            Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(str2);
            return resourcesForApplication.getInteger(resourcesForApplication.getIdentifier(str, "integer", str2));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Package name not found", e);
            return i;
        }
    }

    public static boolean getBooleanResources(String str, String str2, Context context, boolean z) {
        try {
            Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(str2);
            return resourcesForApplication.getBoolean(resourcesForApplication.getIdentifier(str, "bool", str2));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Package name not found", e);
            return z;
        }
    }

    public static boolean isVzwSIM(Context context) {
        boolean z = ((TelephonyManager) context.getSystemService("phone")).getSimCarrierId() == 1839;
        if (DBG) {
            String str = TAG;
            Log.d(str, "isVzwSIM() = " + z);
        }
        return z;
    }
}

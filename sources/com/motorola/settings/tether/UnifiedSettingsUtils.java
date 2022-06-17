package com.motorola.settings.tether;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class UnifiedSettingsUtils {
    public static boolean isVzwUnifiedSettingsInstalledAndEnabled(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo("com.motorola.vzw.provider", 0);
            if (applicationInfo == null || !applicationInfo.enabled) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void sendUnifiedSettingsBroadcast(Context context, String str, String str2, boolean z) {
        Intent intent = new Intent(str);
        intent.putExtra(str2, z);
        context.sendBroadcast(intent, "android.permission.READ_PRIVILEGED_PHONE_STATE");
    }

    public static void sendUnifiedSettingsBroadcast(Context context, String str, String str2, long j) {
        Intent intent = new Intent(str);
        intent.putExtra(str2, j);
        context.sendBroadcast(intent, "android.permission.READ_PRIVILEGED_PHONE_STATE");
    }

    public static void sendUnifiedSettingsBroadcast(Context context, String str) {
        context.sendBroadcast(new Intent(str), "android.permission.READ_PRIVILEGED_PHONE_STATE");
    }
}

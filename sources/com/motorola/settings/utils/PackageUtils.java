package com.motorola.settings.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

public class PackageUtils {
    public static boolean isPackageEnabled(Context context, String str) {
        PackageManager packageManager;
        if (context == null || TextUtils.isEmpty(str) || (packageManager = context.getPackageManager()) == null) {
            return false;
        }
        try {
            return packageManager.getApplicationInfo(str, 0).enabled;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static boolean isGmsCoreEnabled(Context context) {
        if (!Build.IS_PRC_PRODUCT) {
            return true;
        }
        return isPackageEnabled(context, "com.google.android.gms");
    }
}

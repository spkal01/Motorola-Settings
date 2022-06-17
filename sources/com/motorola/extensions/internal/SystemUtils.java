package com.motorola.extensions.internal;

import android.content.pm.PackageManager;

public class SystemUtils {
    public static boolean isSystemOrMotoApp(PackageManager packageManager, String str) {
        try {
            if ((packageManager.getApplicationInfo(str, 0).flags & 1) <= 0 && packageManager.checkSignatures("com.motorola.motosignature.app", str) != 0) {
                return false;
            }
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
        }
    }
}

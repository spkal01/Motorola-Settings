package com.android.settingslib.utils.applications;

import android.content.pm.PackageManager;
import android.util.Log;

public class AppUtils {
    public static final boolean IS_PRC_PRODUCT = isPrcProduct();
    private static final String TAG = "AppUtils";

    public static CharSequence getApplicationLabel(PackageManager packageManager, String str) {
        try {
            return packageManager.getApplicationInfo(str, 4194816).loadLabel(packageManager);
        } catch (PackageManager.NameNotFoundException unused) {
            String str2 = TAG;
            Log.w(str2, "Unable to find info for package: " + str);
            return null;
        }
    }

    private static boolean isPrcProduct() {
        try {
            return ((Boolean) Class.forName("android.os.Build").getDeclaredField("IS_PRC_PRODUCT").get((Object) null)).booleanValue();
        } catch (Exception e) {
            String str = TAG;
            Log.e(str, "Unable to check prc product: " + e.getMessage());
            return false;
        }
    }
}

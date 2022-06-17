package com.motorola.settings.biometrics.face;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;

public final class FaceUtils {
    private static final String TAG = "FaceUtils";

    public static boolean isMotoFaceUnlock() {
        return SystemProperties.getBoolean("ro.face.moto_unlock_service", false);
    }

    public static boolean isFaceDisabledByAdmin(Context context) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
        try {
            if (devicePolicyManager.getAggregatedPasswordComplexityForUser(UserHandle.myUserId(), false) > 65536) {
                return true;
            }
        } catch (SecurityException e) {
            Log.e(TAG, "isFaceDisabledByAdmin error:", e);
        }
        return (devicePolicyManager.getKeyguardDisabledFeatures((ComponentName) null) & 128) != 0;
    }
}

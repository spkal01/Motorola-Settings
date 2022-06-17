package com.motorola.settings.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.motorola.settings.network.MotoTelephonyFeature;

public class MotoWipeBpUtils {
    private static final String TAG = "MotoWipeBpUtils";

    public static boolean doBpWipeAndMasterClear(Context context, boolean z, boolean z2) {
        if (!isVzwChannelId(context)) {
            return false;
        }
        Intent intent = new Intent("com.motorola.programmenu.MODEM_FACTORY_RESET");
        intent.setClassName("com.motorola.carriersettingsext", "com.motorola.carriersettingsext.vzwguti.ResetActivity");
        intent.putExtra("skip_user_confirmation", true);
        intent.putExtra("com.android.internal.intent.extra.WIPE_ESIMS", z);
        intent.putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", z2);
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException unused) {
            Log.e(TAG, "Activity not found, fallback to Base Main Clear");
            return false;
        }
    }

    public static boolean isVzwChannelId(Context context) {
        String channelId = MotoTelephonyFeature.getChannelId(context);
        String str = TAG;
        Log.d(str, "isVzwChannelId: channelId: " + channelId);
        return "vzw".equals(channelId) || "vzwpre".equals(channelId);
    }
}

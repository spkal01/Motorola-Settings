package com.motorola.settings.utils;

import android.content.Context;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.settings.deviceinfo.DeviceUtils;

public class SubsidyLockUtil {
    public static boolean isJioSubsidyLocked(Context context) {
        if (!isJioChannelId(context) || MotorolaSettings.Global.getInt(context.getContentResolver(), "jio_subsidy_locked", 1) != 1) {
            return false;
        }
        return true;
    }

    private static boolean isJioChannelId(Context context) {
        return "retjio".equals(DeviceUtils.getChannelId(context));
    }
}

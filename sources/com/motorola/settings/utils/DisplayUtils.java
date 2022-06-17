package com.motorola.settings.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.android.settings.C1980R$bool;
import motorola.core_services.misc.WaterfallManager;

public class DisplayUtils {
    public static boolean isColorTemperatureModeAvailable(Context context) {
        return context.getResources().getBoolean(C1980R$bool.config_color_temperature_mode) || Build.VERSION.DEVICE_INITIAL_SDK_INT >= 30;
    }

    public static boolean isWaterfallModeAvailable(Context context) {
        return WaterfallManager.hasWaterfallDisplay(context);
    }

    public static boolean isFullScreeModeAvailable(Context context) {
        return !TextUtils.isEmpty(context.getResources().getString(17039973));
    }

    public static boolean isDesktopMode(Context context) {
        return (context.getDisplay().getFlags() & 64) != 0;
    }
}

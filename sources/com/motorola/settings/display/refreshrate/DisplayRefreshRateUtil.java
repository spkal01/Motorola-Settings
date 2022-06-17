package com.motorola.settings.display.refreshrate;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import com.android.settings.C1980R$bool;
import com.android.settings.C1992R$string;

public class DisplayRefreshRateUtil {
    public static final boolean isDisplayRefreshRateAvailable(Context context) {
        if (context != null) {
            return context.getResources().getBoolean(C1980R$bool.display_refresh_settings_enabled);
        }
        return false;
    }

    public static final void setRefreshRate(Context context, float f) {
        ContentResolver contentResolver = context.getContentResolver();
        if (f == 0.0f) {
            Settings.System.putFloatForUser(contentResolver, "peak_refresh_rate", (float) context.getResources().getInteger(17694788), -2);
            Settings.System.putFloatForUser(context.getContentResolver(), "min_refresh_rate", (float) context.getResources().getInteger(17694780), -2);
            return;
        }
        Settings.System.putFloatForUser(contentResolver, "peak_refresh_rate", f, -2);
        Settings.System.putFloatForUser(context.getContentResolver(), "min_refresh_rate", f, -2);
    }

    private static final float getCurrentPeakRefreshRate(Context context) {
        return Settings.System.getFloatForUser(context.getContentResolver(), "peak_refresh_rate", (float) context.getResources().getInteger(17694788), -2);
    }

    private static float getCurrentMinRefreshRate(Context context) {
        int integer = context.getResources().getInteger(17694778);
        float integer2 = (float) context.getResources().getInteger(17694780);
        float currentPeakRefreshRate = getCurrentPeakRefreshRate(context);
        if (integer == 1) {
            integer2 = currentPeakRefreshRate;
        }
        return Settings.System.getFloatForUser(context.getContentResolver(), "min_refresh_rate", integer2, -2);
    }

    public static final String getCurrentRefreshRateTitle(Context context) {
        return getRefreshRateTitle(context, getCurrentRefreshRate(context));
    }

    public static final int getCurrentRefreshRate(Context context) {
        float currentPeakRefreshRate = getCurrentPeakRefreshRate(context);
        if (currentPeakRefreshRate != getCurrentMinRefreshRate(context)) {
            return 0;
        }
        return (int) currentPeakRefreshRate;
    }

    public static final String getRefreshRateTitle(Context context, int i) {
        if (i == 0) {
            return context.getResources().getString(C1992R$string.display_refresh_rate_value_auto_title);
        }
        return context.getResources().getString(C1992R$string.display_refresh_rate_value_title, new Object[]{Integer.valueOf(i)});
    }

    public static final String getRefreshRateSummary(Context context, int i) {
        if (i == 0) {
            return context.getResources().getString(C1992R$string.display_refresh_rate_auto_summary);
        }
        if (i != 60) {
            return context.getResources().getString(C1992R$string.display_refresh_rate_smoothest_summary);
        }
        return context.getResources().getString(C1992R$string.display_refresh_rate_less_smooth_summary);
    }
}

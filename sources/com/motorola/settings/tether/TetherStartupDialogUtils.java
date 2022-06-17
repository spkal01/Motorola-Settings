package com.motorola.settings.tether;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import com.motorola.settingslib.hotspothelper.HotspotHelper;

public class TetherStartupDialogUtils {
    private static final String TAG = "TetherStartupDialogUtils";

    public static boolean shouldShow(Context context) {
        if (!(context instanceof Activity)) {
            Log.d(TAG, "Can't show dialog without an Activity context");
            return false;
        } else if (getDoNotShowAgain(context) || !HotspotHelper.shouldWarnWhenEnablingTethering(context)) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean getDoNotShowAgain(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "wifi_ap_display_starup_warning", 0) == 1;
    }

    public static void setDoNotShowAgain(Context context) {
        Settings.System.putInt(context.getContentResolver(), "wifi_ap_display_starup_warning", 1);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("tether_startup_dialog", 0);
    }

    public static boolean getCheckBoxCheckedFromSharedPrefs(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        if (sharedPreferences == null) {
            return false;
        }
        return sharedPreferences.getBoolean("tether_startup_dialog_checkbox", false);
    }

    public static void setCheckBoxCheckedOnSharedPrefs(Context context, boolean z) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        if (sharedPreferences != null) {
            sharedPreferences.edit().putBoolean("tether_startup_dialog_checkbox", z).apply();
        }
    }

    public static void resetCheckBoxCheckedOnSharedPrefs(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        if (sharedPreferences != null) {
            sharedPreferences.edit().remove("tether_startup_dialog_checkbox").apply();
        }
    }

    public static void displayDialogsOrEnableWifiTether(Context context, FragmentManager fragmentManager, WarningDialogCallback warningDialogCallback) {
        if (shouldShow(context)) {
            WifiTetherStartupDialog.show(fragmentManager, warningDialogCallback);
        } else if (HotspotWifiConflictWarningDialog.shouldShow(context)) {
            HotspotWifiConflictWarningDialog.show(fragmentManager, warningDialogCallback);
        } else if (HotspotRoamingWarningDialog.shouldShow(context)) {
            HotspotRoamingWarningDialog.show(fragmentManager, warningDialogCallback);
        } else if (warningDialogCallback != null) {
            warningDialogCallback.onPositive();
        }
    }
}

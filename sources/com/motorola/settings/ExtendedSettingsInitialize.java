package com.motorola.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.ColorDisplayManager;
import com.android.settings.C1980R$bool;
import com.android.settings.Settings;
import com.android.settings.SettingsInitialize;
import com.android.settings.wallpaper.StyleSuggestionActivity;
import com.motorola.settings.biometrics.fingerprint.FingerprintUtils;
import com.motorola.settings.utils.DisplayUtils;

public class ExtendedSettingsInitialize extends SettingsInitialize {
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        setupColorTemperatureMode(context, intent);
        setupEndlessSettings(context, intent);
        setupStyleSuggestion(context, intent);
        FingerprintUtils.checkAndEnableFODAnimationStyle(context);
    }

    private void setupStyleSuggestion(Context context, Intent intent) {
        context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, StyleSuggestionActivity.class), context.getResources().getBoolean(C1980R$bool.config_moto_style_suggestion_enabled) ? 1 : 2, 1);
    }

    private void setupColorTemperatureMode(Context context, Intent intent) {
        if (DisplayUtils.isColorTemperatureModeAvailable(context)) {
            ColorDisplayManager colorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
            if (colorDisplayManager.getColorMode() == 1) {
                colorDisplayManager.setColorMode(0);
            }
        }
    }

    private void setupEndlessSettings(Context context, Intent intent) {
        if (DisplayUtils.isWaterfallModeAvailable(context)) {
            context.getPackageManager().setComponentEnabledSetting(new ComponentName(context, Settings.EndlessApplicationsActivity.class), 1, 1);
        }
    }
}

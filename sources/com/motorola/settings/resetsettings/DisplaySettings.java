package com.motorola.settings.resetsettings;

import android.app.UiModeManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.hardware.display.ColorDisplayManager;
import android.provider.Settings;
import android.util.Log;
import com.android.settingslib.display.DisplayDensityUtils;
import com.android.settingslib.dream.DreamBackend;
import com.motorola.android.provider.MotorolaSettings;

public class DisplaySettings implements IDeviceSettings {
    private static final String TAG = "DisplaySettings";
    private boolean defaultBoolValue = false;
    private int defaultIntValue = -1;
    private Context mContext;

    DisplaySettings(Context context) {
        this.mContext = context;
    }

    public void resetSettings() {
        resetBrightness();
        resetWallpaper();
        resetNightLight();
        resetAdaptiveBrightness();
        resetUiNightMode();
        resetScreenTimeout();
        resetAutoRotate();
        resetColorMode();
        resetRoamingBanner();
        resetFontSize();
        resetDisplaySize();
        resetScreenSaver();
    }

    private void resetBrightness() {
        try {
            int integerResources = DeviceSettingsUtils.getIntegerResources("def_screen_brightness", "com.android.providers.settings", this.mContext, this.defaultIntValue);
            if (integerResources != this.defaultIntValue) {
                Settings.System.putInt(this.mContext.getContentResolver(), "screen_brightness", integerResources);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error resetting brightness", e);
        }
    }

    private void resetWallpaper() {
        WallpaperManager.getInstance(this.mContext).clearWallpaper();
    }

    private void resetNightLight() {
        ColorDisplayManager colorDisplayManager = (ColorDisplayManager) this.mContext.getSystemService(ColorDisplayManager.class);
        colorDisplayManager.setNightDisplayAutoMode(0);
        colorDisplayManager.setNightDisplayActivated(this.defaultBoolValue);
    }

    private void resetAdaptiveBrightness() {
        Settings.System.putInt(this.mContext.getContentResolver(), "screen_brightness_mode", 1);
    }

    private void resetUiNightMode() {
        ((UiModeManager) this.mContext.getSystemService(UiModeManager.class)).setNightMode(1);
    }

    private void resetScreenTimeout() {
        int integerResources = DeviceSettingsUtils.getIntegerResources("def_screen_off_timeout", "com.android.providers.settings", this.mContext, this.defaultIntValue);
        if (integerResources != this.defaultIntValue) {
            Settings.System.putInt(this.mContext.getContentResolver(), "screen_off_timeout", integerResources);
        }
    }

    private void resetAutoRotate() {
        Settings.System.putInt(this.mContext.getContentResolver(), "accelerometer_rotation", DeviceSettingsUtils.getBooleanResources("def_accelerometer_rotation", "com.android.providers.settings", this.mContext, this.defaultBoolValue) ? 1 : 0);
    }

    private void resetColorMode() {
        ((ColorDisplayManager) this.mContext.getSystemService(ColorDisplayManager.class)).setColorMode(2);
    }

    private void resetRoamingBanner() {
        int integerResources = DeviceSettingsUtils.getIntegerResources("def_eri_text_banner", "com.motorola.android.providers.settings", this.mContext, this.defaultIntValue);
        if (integerResources != this.defaultIntValue) {
            MotorolaSettings.Secure.putInt(this.mContext.getContentResolver(), "eri_text_banner", integerResources);
        }
    }

    private void resetFontSize() {
        Settings.System.putFloat(this.mContext.getContentResolver(), "font_scale", 1.0f);
    }

    private void resetDisplaySize() {
        DisplayDensityUtils.clearForcedDisplayDensity(0);
    }

    private void resetScreenSaver() {
        if (this.mContext.getResources().getBoolean(17891552)) {
            DreamBackend instance = DreamBackend.getInstance(this.mContext);
            instance.setWhenToDream(1);
            instance.setActiveDream(instance.getDefaultDream());
        }
    }
}

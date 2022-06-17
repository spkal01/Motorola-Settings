package com.motorola.settings.resetsettings;

import android.content.Context;
import android.provider.Settings;
import com.android.settingslib.fuelgauge.BatterySaverUtils;

public class BatterySettings implements IDeviceSettings {
    private Context mContext;

    public BatterySettings(Context context) {
        this.mContext = context;
    }

    public void resetSettings() {
        resetAdaptiveBatteryManager();
        resetBatteryPercentDisplay();
        resetBatterySaverSchedule();
        resetBatterySaverState();
    }

    private void resetAdaptiveBatteryManager() {
        Settings.Global.putInt(this.mContext.getContentResolver(), "adaptive_battery_management_enabled", 1);
    }

    private void resetBatteryPercentDisplay() {
        Settings.System.putInt(this.mContext.getContentResolver(), "status_bar_show_battery_percent", 1);
    }

    private void resetBatterySaverSchedule() {
        Settings.Global.putInt(this.mContext.getContentResolver(), "automatic_power_save_mode", 0);
        Settings.Global.putInt(this.mContext.getContentResolver(), "low_power_trigger_level", 0);
        Settings.Global.putInt(this.mContext.getContentResolver(), "low_power_sticky_auto_disable_enabled", 1);
        Settings.Global.putInt(this.mContext.getContentResolver(), "low_power_sticky", 0);
    }

    private void resetBatterySaverState() {
        BatterySaverUtils.setPowerSaveMode(this.mContext, false, false);
    }
}

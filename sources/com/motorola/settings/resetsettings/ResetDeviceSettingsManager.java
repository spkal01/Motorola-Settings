package com.motorola.settings.resetsettings;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

public class ResetDeviceSettingsManager {
    List<IDeviceSettings> mDeviceSettings = new ArrayList();

    ResetDeviceSettingsManager(Context context) {
        instantiateResetSettings(context);
    }

    /* access modifiers changed from: package-private */
    public void instantiateResetSettings(Context context) {
        this.mDeviceSettings.add(new DisplaySettings(context));
        this.mDeviceSettings.add(new StorageSettings(context));
        this.mDeviceSettings.add(new BatterySettings(context));
    }

    /* access modifiers changed from: package-private */
    public void resetDeviceSettings() {
        for (IDeviceSettings resetSettings : this.mDeviceSettings) {
            resetSettings.resetSettings();
        }
    }
}

package com.motorola.settings.resetsettings;

import android.content.Context;
import android.provider.Settings;

public class StorageSettings implements IDeviceSettings {
    private Context mContext;

    public StorageSettings(Context context) {
        this.mContext = context;
    }

    public void resetSettings() {
        resetStorageManager();
    }

    private void resetStorageManager() {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "automatic_storage_manager_enabled", 0);
        Settings.Secure.putInt(this.mContext.getContentResolver(), "automatic_storage_manager_days_to_retain", 90);
    }
}

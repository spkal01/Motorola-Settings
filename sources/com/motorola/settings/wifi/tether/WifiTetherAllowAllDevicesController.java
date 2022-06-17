package com.motorola.settings.wifi.tether;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.motorola.settings.tether.UnifiedSettingsUtils;

public class WifiTetherAllowAllDevicesController extends WifiTetherBasePreferenceController implements Preference.OnPreferenceChangeListener {
    private SharedPreferences mSharedPrefs;

    public String getPreferenceKey() {
        return "wifi_tether_allow_all_devices";
    }

    public WifiTetherAllowAllDevicesController(Context context, SharedPreferences sharedPreferences) {
        super(context, (WifiTetherBasePreferenceController.OnTetherConfigUpdateListener) null);
        this.mSharedPrefs = sharedPreferences;
    }

    public void updateDisplay() {
        boolean z = false;
        boolean z2 = this.mSharedPrefs.getBoolean("clientControlByUser", false);
        if (this.mSharedPrefs.getAll().size() <= 1) {
            this.mPreference.setEnabled(false);
            if (z2) {
                this.mSharedPrefs.edit().putBoolean("clientControlByUser", false).apply();
                ((CheckBoxPreference) this.mPreference).setChecked(!z);
            }
        } else {
            this.mPreference.setEnabled(true);
        }
        z = z2;
        ((CheckBoxPreference) this.mPreference).setChecked(!z);
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        this.mSharedPrefs.edit().putBoolean("clientControlByUser", !booleanValue).apply();
        if (!UnifiedSettingsUtils.isVzwUnifiedSettingsInstalledAndEnabled(this.mContext)) {
            return true;
        }
        UnifiedSettingsUtils.sendUnifiedSettingsBroadcast(this.mContext, "com.motorola.wifi.TETHER_ALLOW_ALL_STATE_CHANGED", "com.motorola.wifi.extra.TETHER_ALLOW_ALL", booleanValue);
        return true;
    }
}

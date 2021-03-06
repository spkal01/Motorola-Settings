package com.android.settings.development;

import android.content.Context;
import android.net.wifi.WifiManager;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class WifiVerboseLoggingPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final int SETTING_VALUE_OFF = 0;
    static final int SETTING_VALUE_ON = 1;
    private final WifiManager mWifiManager;

    public String getPreferenceKey() {
        return "wifi_verbose_logging";
    }

    public WifiVerboseLoggingPreferenceController(Context context) {
        super(context);
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        this.mWifiManager.setVerboseLoggingEnabled(((Boolean) obj).booleanValue());
        return true;
    }

    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(this.mWifiManager.isVerboseLoggingEnabled());
    }

    /* access modifiers changed from: protected */
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        this.mWifiManager.setVerboseLoggingEnabled(false);
        ((SwitchPreference) this.mPreference).setChecked(false);
    }
}

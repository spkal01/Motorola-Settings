package com.android.settings.network;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public class ProxyPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    public String getPreferenceKey() {
        return "proxy_settings";
    }

    public boolean isAvailable() {
        return false;
    }

    public ProxyPreferenceController(Context context) {
        super(context);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference("proxy_settings");
        if (findPreference != null) {
            findPreference.setEnabled(((DevicePolicyManager) this.mContext.getSystemService("device_policy")).getGlobalProxyAdmin() == null);
        }
    }
}

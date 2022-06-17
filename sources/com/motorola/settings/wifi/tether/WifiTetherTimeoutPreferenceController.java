package com.motorola.settings.wifi.tether;

import android.content.Context;
import android.content.om.OverlayInfo;
import android.content.om.OverlayManager;
import android.content.res.Resources;
import android.net.wifi.SoftApConfiguration;
import android.os.UserHandle;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.motorola.settings.tether.UnifiedSettingsUtils;

public class WifiTetherTimeoutPreferenceController extends WifiTetherBasePreferenceController {
    public WifiTetherTimeoutPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener);
    }

    private String getDefaultTimeout() {
        OverlayManager overlayManager = (OverlayManager) this.mContext.getSystemService(OverlayManager.class);
        String str = "600000";
        if (overlayManager != null) {
            for (OverlayInfo packageName : overlayManager.getOverlayInfosForTarget("com.google.android.wifi.resources", UserHandle.of(UserHandle.myUserId()))) {
                String packageName2 = packageName.getPackageName();
                try {
                    Resources resources = this.mContext.createPackageContext(packageName2, 0).getResources();
                    str = Integer.toString(Integer.valueOf(resources.getInteger(resources.getIdentifier("config_wifiFrameworkSoftApShutDownTimeoutMilliseconds", "integer", packageName2))).intValue());
                } catch (Exception unused) {
                    Log.e("WifiTetherTimeoutPref", "Couldn't get" + "config_wifiFrameworkSoftApShutDownTimeoutMilliseconds" + " from " + packageName2);
                }
            }
        }
        return str;
    }

    public void updateDisplay() {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        boolean isAutoShutdownEnabled = softApConfiguration.isAutoShutdownEnabled();
        Long valueOf = Long.valueOf(softApConfiguration.getShutdownTimeoutMillis());
        ListPreference listPreference = (ListPreference) this.mPreference;
        if (!isAutoShutdownEnabled) {
            listPreference.setValue("-1");
        } else if (valueOf.longValue() == 0) {
            listPreference.setValue(getDefaultTimeout());
        } else {
            listPreference.setValue(String.valueOf(valueOf));
        }
    }

    public String getPreferenceKey() {
        return FeatureFlagUtils.isEnabled(this.mContext, "settings_tether_all_in_one") ? "wifi_tether_timeout_settings_2" : "wifi_tether_timeout_settings";
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        long parseLong = Long.parseLong((String) obj);
        Log.d("WifiTetherTimeoutPref", "Tether timeout preference changed, updating timeout to " + parseLong);
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        int i = (parseLong > -1 ? 1 : (parseLong == -1 ? 0 : -1));
        if (i == 0) {
            parseLong = 0;
        }
        SoftApConfiguration build = new SoftApConfiguration.Builder(softApConfiguration).setAutoShutdownEnabled(i != 0).setShutdownTimeoutMillis(parseLong).build();
        if (UnifiedSettingsUtils.isVzwUnifiedSettingsInstalledAndEnabled(this.mContext)) {
            UnifiedSettingsUtils.sendUnifiedSettingsBroadcast(this.mContext, "com.motorola.wifi.TETHER_TIMEOUT_STATE_CHANGED", "com.motorola.wifi.extra.TETHER_TIMEOUT", parseLong);
        }
        return this.mWifiManager.setSoftApConfiguration(build);
    }
}

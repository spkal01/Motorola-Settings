package com.motorola.settings.wifi.tether;

import android.content.Context;
import android.net.wifi.SoftApCapability;
import android.net.wifi.SoftApConfiguration;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import java.text.NumberFormat;

public class WifiTetherDeviceLimitController extends WifiTetherBasePreferenceController implements Preference.OnPreferenceChangeListener {
    private SoftApCapability mSoftApCapability;

    public String getPreferenceKey() {
        return "wifi_tether_device_limit";
    }

    public WifiTetherDeviceLimitController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener);
    }

    public void updateDisplay() {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        if (this.mSoftApCapability == null) {
            return;
        }
        if (softApConfiguration.getMaxNumberOfClients() == 0) {
            ((ListPreference) this.mPreference).setValueIndex(this.mSoftApCapability.getMaxSupportedClients() - 1);
        } else {
            ((ListPreference) this.mPreference).setValueIndex(softApConfiguration.getMaxNumberOfClients() - 1);
        }
    }

    public boolean isAvailable() {
        SoftApCapability softApCapability;
        if (!super.isAvailable() || (softApCapability = this.mSoftApCapability) == null || !softApCapability.areFeaturesSupported(2) || this.mSoftApCapability.getMaxSupportedClients() <= 1) {
            return false;
        }
        return true;
    }

    public void setSoftApCapability(SoftApCapability softApCapability) {
        this.mSoftApCapability = softApCapability;
        int i = 0;
        if (isAvailable()) {
            this.mPreference.setVisible(true);
            this.mPreference.setOnPreferenceChangeListener(this);
            int maxSupportedClients = softApCapability.getMaxSupportedClients();
            NumberFormat instance = NumberFormat.getInstance();
            String[] strArr = new String[maxSupportedClients];
            String[] strArr2 = new String[maxSupportedClients];
            while (i < maxSupportedClients) {
                int i2 = i + 1;
                strArr[i] = Integer.toString(i2);
                strArr2[i] = instance.format((long) i2);
                i = i2;
            }
            ((ListPreference) this.mPreference).setEntries((CharSequence[]) strArr2);
            ((ListPreference) this.mPreference).setEntryValues((CharSequence[]) strArr);
            updateDisplay();
            return;
        }
        this.mPreference.setVisible(false);
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        return this.mWifiManager.setSoftApConfiguration(new SoftApConfiguration.Builder(this.mWifiManager.getSoftApConfiguration()).setMaxNumberOfClients(Integer.parseInt((String) obj)).build());
    }
}

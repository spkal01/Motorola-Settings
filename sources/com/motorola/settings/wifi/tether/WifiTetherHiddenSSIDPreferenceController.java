package com.motorola.settings.wifi.tether;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.SoftApConfiguration;
import android.util.FeatureFlagUtils;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.motorola.settings.wifi.BroadcastSsidInvisibleDialogFragment;

public class WifiTetherHiddenSSIDPreferenceController extends WifiTetherBasePreferenceController implements Preference.OnPreferenceChangeListener {
    private boolean mHiddenState = false;

    public WifiTetherHiddenSSIDPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener);
        this.mShowRestartTetherDialog = false;
    }

    public String getPreferenceKey() {
        return FeatureFlagUtils.isEnabled(this.mContext, "settings_tether_all_in_one") ? "wifi_tether_hidden_network_2" : "wifi_tether_hidden_network";
    }

    public boolean getHiddenNetworkStatus() {
        return this.mHiddenState;
    }

    public void updateDisplay() {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        boolean z = false;
        if (softApConfiguration != null) {
            this.mHiddenState = softApConfiguration.isHiddenSsid();
        } else {
            this.mHiddenState = false;
        }
        ((CheckBoxPreference) this.mPreference).setChecked(this.mHiddenState);
        this.mShowRestartTetherDialog = this.mHiddenState || !BroadcastSsidInvisibleDialogFragment.needsDialog(this.mContext);
        int wifiApState = this.mWifiManager.getWifiApState();
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) this.mPreference;
        if (wifiApState == 11 || wifiApState == 13) {
            z = true;
        }
        checkBoxPreference.setEnabled(z);
    }

    public boolean onPreferenceChange(final Preference preference, Object obj) {
        this.mHiddenState = ((Boolean) obj).booleanValue();
        if (!BroadcastSsidInvisibleDialogFragment.needsDialog(this.mContext) || !this.mHiddenState) {
            ((CheckBoxPreference) preference).setChecked(this.mHiddenState);
            this.mListener.onTetherConfigUpdated(this);
            return true;
        }
        BroadcastSsidInvisibleDialogFragment.show(((Activity) this.mContext).getFragmentManager(), new BroadcastSsidInvisibleDialogFragment.SsidInvisibleDialogFragmentListener() {
            public void OnDialogClickListener(boolean z) {
                ((CheckBoxPreference) preference).setChecked(z);
                if (z) {
                    WifiTetherHiddenSSIDPreferenceController.this.mListener.onTetherConfigUpdated(WifiTetherHiddenSSIDPreferenceController.this);
                }
            }
        });
        return true;
    }
}

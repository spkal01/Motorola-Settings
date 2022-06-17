package com.android.settings.wifi.tether;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.SoftApCapability;
import android.net.wifi.WifiManager;
import android.util.FeatureFlagUtils;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.C1978R$array;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.motorola.settings.wifi.OpenSecurityDialogFragment;
import java.util.LinkedHashMap;
import java.util.Map;

public class WifiTetherSecurityPreferenceController extends WifiTetherBasePreferenceController implements WifiManager.SoftApCallback {
    boolean mIsWpa3Supported = true;
    private final String[] mSecurityEntries = this.mContext.getResources().getStringArray(C1978R$array.wifi_tether_security);
    private Map<Integer, String> mSecurityMap = new LinkedHashMap();
    /* access modifiers changed from: private */
    public int mSecurityValue;

    public WifiTetherSecurityPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener);
        String[] stringArray = this.mContext.getResources().getStringArray(C1978R$array.wifi_tether_security_values);
        for (int i = 0; i < this.mSecurityEntries.length; i++) {
            this.mSecurityMap.put(Integer.valueOf(Integer.parseInt(stringArray[i])), this.mSecurityEntries[i]);
        }
        this.mWifiManager.registerSoftApCallback(context.getMainExecutor(), this);
        this.mShowRestartTetherDialog = true;
    }

    public String getPreferenceKey() {
        return FeatureFlagUtils.isEnabled(this.mContext, "settings_tether_all_in_one") ? "wifi_tether_security_2" : "wifi_tether_security";
    }

    public void updateDisplay() {
        Preference preference = this.mPreference;
        if (preference != null) {
            ListPreference listPreference = (ListPreference) preference;
            if (!this.mIsWpa3Supported && this.mSecurityMap.keySet().removeIf(WifiTetherSecurityPreferenceController$$ExternalSyntheticLambda3.INSTANCE)) {
                listPreference.setEntries((CharSequence[]) this.mSecurityMap.values().stream().toArray(WifiTetherSecurityPreferenceController$$ExternalSyntheticLambda1.INSTANCE));
                listPreference.setEntryValues((CharSequence[]) this.mSecurityMap.keySet().stream().map(WifiTetherSecurityPreferenceController$$ExternalSyntheticLambda0.INSTANCE).toArray(WifiTetherSecurityPreferenceController$$ExternalSyntheticLambda2.INSTANCE));
            }
            int securityType = this.mWifiManager.getSoftApConfiguration().getSecurityType();
            if (this.mSecurityMap.get(Integer.valueOf(securityType)) == null) {
                securityType = 1;
            }
            this.mSecurityValue = securityType;
            listPreference.setSummary(this.mSecurityMap.get(Integer.valueOf(securityType)));
            listPreference.setValue(String.valueOf(this.mSecurityValue));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateDisplay$0(Integer num) {
        return num.intValue() > 1;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ CharSequence[] lambda$updateDisplay$1(int i) {
        return new CharSequence[i];
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ CharSequence[] lambda$updateDisplay$2(int i) {
        return new CharSequence[i];
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        int parseInt = Integer.parseInt((String) obj);
        if (parseInt == 0) {
            OpenSecurityDialogFragment.show(((Activity) this.mContext).getFragmentManager(), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (-1 == i) {
                        int unused = WifiTetherSecurityPreferenceController.this.mSecurityValue = 0;
                        WifiTetherSecurityPreferenceController wifiTetherSecurityPreferenceController = WifiTetherSecurityPreferenceController.this;
                        wifiTetherSecurityPreferenceController.updateSecurityOption(wifiTetherSecurityPreferenceController.mPreference);
                        WifiTetherSecurityPreferenceController.this.updateDisplay();
                    }
                }
            });
            return false;
        }
        this.mSecurityValue = parseInt;
        updateSecurityOption(preference);
        return true;
    }

    /* access modifiers changed from: private */
    public void updateSecurityOption(Preference preference) {
        preference.setSummary((CharSequence) getSummaryForSecurityType(this.mSecurityValue));
        WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener = this.mListener;
        if (onTetherConfigUpdateListener != null) {
            onTetherConfigUpdateListener.onTetherConfigUpdated(this);
        }
    }

    private String getSummaryForSecurityType(int i) {
        int findIndexOfValue = ((ListPreference) this.mPreference).findIndexOfValue(String.valueOf(i));
        if (findIndexOfValue < 0) {
            return "";
        }
        return this.mSecurityEntries[findIndexOfValue];
    }

    public void onCapabilityChanged(SoftApCapability softApCapability) {
        boolean areFeaturesSupported = softApCapability.areFeaturesSupported(4);
        if (!areFeaturesSupported) {
            Log.i("wifi_tether_security", "WPA3 SAE is not supported on this device");
        }
        if (this.mIsWpa3Supported != areFeaturesSupported) {
            this.mIsWpa3Supported = areFeaturesSupported;
            updateDisplay();
        }
        this.mWifiManager.unregisterSoftApCallback(this);
    }

    public int getSecurityType() {
        return this.mSecurityValue;
    }
}

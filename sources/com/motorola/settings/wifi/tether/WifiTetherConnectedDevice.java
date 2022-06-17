package com.motorola.settings.wifi.tether;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.C1992R$string;

public class WifiTetherConnectedDevice extends Preference {
    String mDeviceName;
    String mIPAddress;
    String mMACAddress;

    WifiTetherConnectedDevice(Context context, String str) {
        this(context, str, (String) null, (String) null);
    }

    WifiTetherConnectedDevice(Context context, String str, String str2, String str3) {
        super(context);
        this.mDeviceName = str3;
        this.mIPAddress = str2;
        String upperCase = str.toUpperCase();
        this.mMACAddress = upperCase;
        setKey(upperCase);
    }

    public int hashCode() {
        return this.mMACAddress.hashCode();
    }

    /* access modifiers changed from: package-private */
    public void updateDisplay() {
        String str;
        if (this.mDeviceName == null) {
            this.mDeviceName = getContext().getResources().getString(C1992R$string.wifi_tether_unknown_device);
        }
        setTitle((CharSequence) this.mDeviceName);
        if (this.mIPAddress == null) {
            str = getContext().getResources().getString(C1992R$string.wifi_tether_mac_address) + this.mMACAddress;
        } else {
            str = getContext().getResources().getString(C1992R$string.wifi_tether_ip_address) + this.mIPAddress + "\n" + getContext().getResources().getString(C1992R$string.wifi_tether_mac_address) + this.mMACAddress;
        }
        setSummary((CharSequence) str);
    }
}

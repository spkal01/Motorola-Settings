package com.android.settings.deviceinfo;

import android.content.Context;
import com.android.settings.C1980R$bool;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.deviceinfo.AbstractWifiMacAddressPreferenceController;

public class WifiMacAddressPreferenceController extends AbstractWifiMacAddressPreferenceController implements PreferenceControllerMixin {
    public WifiMacAddressPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }

    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(C1980R$bool.config_show_wifi_mac_address);
    }
}

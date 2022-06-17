package com.motorola.settings.wifi;

import android.content.Context;
import android.net.wifi.WifiManager;
import com.android.settingslib.core.AbstractPreferenceController;

public class WapiCertManagerPreferenceController extends AbstractPreferenceController {
    private final WifiManager mWifiManager;

    public String getPreferenceKey() {
        return "wapi_cert_manage";
    }

    public WapiCertManagerPreferenceController(Context context, WifiManager wifiManager) {
        super(context);
        this.mWifiManager = wifiManager;
    }

    public boolean isAvailable() {
        return this.mWifiManager.isWapiSupported();
    }
}

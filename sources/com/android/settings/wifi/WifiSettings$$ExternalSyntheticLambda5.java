package com.android.settings.wifi;

import com.android.settingslib.wifi.WifiEntryPreference;
import com.android.wifitrackerlib.WifiEntry;

public final /* synthetic */ class WifiSettings$$ExternalSyntheticLambda5 implements WifiEntryPreference.OnButtonClickListener {
    public final /* synthetic */ WifiSettings f$0;
    public final /* synthetic */ WifiEntry f$1;

    public /* synthetic */ WifiSettings$$ExternalSyntheticLambda5(WifiSettings wifiSettings, WifiEntry wifiEntry) {
        this.f$0 = wifiSettings;
        this.f$1 = wifiEntry;
    }

    public final void onButtonClick(WifiEntryPreference wifiEntryPreference) {
        this.f$0.lambda$updateWifiEntryPreferences$10(this.f$1, wifiEntryPreference);
    }
}

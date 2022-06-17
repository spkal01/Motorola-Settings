package com.android.settings.wifi;

import androidx.preference.Preference;
import com.android.wifitrackerlib.WifiEntry;

public final /* synthetic */ class WifiSettings$$ExternalSyntheticLambda2 implements Preference.OnPreferenceClickListener {
    public final /* synthetic */ WifiSettings f$0;
    public final /* synthetic */ WifiEntry f$1;
    public final /* synthetic */ ConnectedWifiEntryPreference f$2;

    public /* synthetic */ WifiSettings$$ExternalSyntheticLambda2(WifiSettings wifiSettings, WifiEntry wifiEntry, ConnectedWifiEntryPreference connectedWifiEntryPreference) {
        this.f$0 = wifiSettings;
        this.f$1 = wifiEntry;
        this.f$2 = connectedWifiEntryPreference;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return this.f$0.lambda$updateWifiEntryPreferences$8(this.f$1, this.f$2, preference);
    }
}

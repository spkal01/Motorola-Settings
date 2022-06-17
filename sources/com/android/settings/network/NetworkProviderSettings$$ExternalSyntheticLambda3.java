package com.android.settings.network;

import com.android.settings.wifi.ConnectedWifiEntryPreference;

public final /* synthetic */ class NetworkProviderSettings$$ExternalSyntheticLambda3 implements ConnectedWifiEntryPreference.OnGearClickListener {
    public final /* synthetic */ NetworkProviderSettings f$0;
    public final /* synthetic */ ConnectedWifiEntryPreference f$1;

    public /* synthetic */ NetworkProviderSettings$$ExternalSyntheticLambda3(NetworkProviderSettings networkProviderSettings, ConnectedWifiEntryPreference connectedWifiEntryPreference) {
        this.f$0 = networkProviderSettings;
        this.f$1 = connectedWifiEntryPreference;
    }

    public final void onGearClick(ConnectedWifiEntryPreference connectedWifiEntryPreference) {
        this.f$0.lambda$updateWifiEntryPreferences$11(this.f$1, connectedWifiEntryPreference);
    }
}

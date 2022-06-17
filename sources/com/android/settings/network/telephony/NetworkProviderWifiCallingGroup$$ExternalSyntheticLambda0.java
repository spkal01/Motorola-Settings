package com.android.settings.network.telephony;

import androidx.preference.Preference;

public final /* synthetic */ class NetworkProviderWifiCallingGroup$$ExternalSyntheticLambda0 implements Preference.OnPreferenceClickListener {
    public final /* synthetic */ NetworkProviderWifiCallingGroup f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ NetworkProviderWifiCallingGroup$$ExternalSyntheticLambda0(NetworkProviderWifiCallingGroup networkProviderWifiCallingGroup, int i) {
        this.f$0 = networkProviderWifiCallingGroup;
        this.f$1 = i;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return this.f$0.lambda$setSubscriptionInfoForPreference$1(this.f$1, preference);
    }
}

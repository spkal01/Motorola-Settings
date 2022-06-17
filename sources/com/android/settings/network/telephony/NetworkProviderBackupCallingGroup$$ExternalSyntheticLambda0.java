package com.android.settings.network.telephony;

import androidx.preference.Preference;

public final /* synthetic */ class NetworkProviderBackupCallingGroup$$ExternalSyntheticLambda0 implements Preference.OnPreferenceClickListener {
    public final /* synthetic */ NetworkProviderBackupCallingGroup f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ NetworkProviderBackupCallingGroup$$ExternalSyntheticLambda0(NetworkProviderBackupCallingGroup networkProviderBackupCallingGroup, int i, boolean z) {
        this.f$0 = networkProviderBackupCallingGroup;
        this.f$1 = i;
        this.f$2 = z;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return this.f$0.lambda$setSubscriptionInfoForPreference$0(this.f$1, this.f$2, preference);
    }
}

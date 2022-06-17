package com.android.settings.network;

import android.telephony.SubscriptionInfo;
import androidx.preference.Preference;

public final /* synthetic */ class NetworkProviderSimListController$$ExternalSyntheticLambda0 implements Preference.OnPreferenceClickListener {
    public final /* synthetic */ NetworkProviderSimListController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ SubscriptionInfo f$2;

    public /* synthetic */ NetworkProviderSimListController$$ExternalSyntheticLambda0(NetworkProviderSimListController networkProviderSimListController, int i, SubscriptionInfo subscriptionInfo) {
        this.f$0 = networkProviderSimListController;
        this.f$1 = i;
        this.f$2 = subscriptionInfo;
    }

    public final boolean onPreferenceClick(Preference preference) {
        return this.f$0.lambda$update$0(this.f$1, this.f$2, preference);
    }
}

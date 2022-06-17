package com.android.settings.network.telephony;

import android.content.Context;
import android.telephony.SubscriptionInfo;
import java.util.function.Predicate;

public final /* synthetic */ class NetworkProviderWifiCallingGroup$$ExternalSyntheticLambda1 implements Predicate {
    public final /* synthetic */ NetworkProviderWifiCallingGroup f$0;
    public final /* synthetic */ Context f$1;

    public /* synthetic */ NetworkProviderWifiCallingGroup$$ExternalSyntheticLambda1(NetworkProviderWifiCallingGroup networkProviderWifiCallingGroup, Context context) {
        this.f$0 = networkProviderWifiCallingGroup;
        this.f$1 = context;
    }

    public final boolean test(Object obj) {
        return this.f$0.lambda$setSubscriptionInfoList$0(this.f$1, (SubscriptionInfo) obj);
    }
}

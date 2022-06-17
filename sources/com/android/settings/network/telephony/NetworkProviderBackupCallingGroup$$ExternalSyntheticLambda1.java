package com.android.settings.network.telephony;

import android.content.Context;
import android.telephony.SubscriptionInfo;
import java.util.function.Predicate;

public final /* synthetic */ class NetworkProviderBackupCallingGroup$$ExternalSyntheticLambda1 implements Predicate {
    public final /* synthetic */ NetworkProviderBackupCallingGroup f$0;
    public final /* synthetic */ Context f$1;

    public /* synthetic */ NetworkProviderBackupCallingGroup$$ExternalSyntheticLambda1(NetworkProviderBackupCallingGroup networkProviderBackupCallingGroup, Context context) {
        this.f$0 = networkProviderBackupCallingGroup;
        this.f$1 = context;
    }

    public final boolean test(Object obj) {
        return this.f$0.lambda$setSubscriptionInfoList$1(this.f$1, (SubscriptionInfo) obj);
    }
}

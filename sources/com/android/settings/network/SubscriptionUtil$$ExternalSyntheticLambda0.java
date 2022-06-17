package com.android.settings.network;

import android.content.Context;
import android.telephony.SubscriptionInfo;
import java.util.function.Function;

public final /* synthetic */ class SubscriptionUtil$$ExternalSyntheticLambda0 implements Function {
    public final /* synthetic */ Context f$0;

    public /* synthetic */ SubscriptionUtil$$ExternalSyntheticLambda0(Context context) {
        this.f$0 = context;
    }

    public final Object apply(Object obj) {
        return SubscriptionUtil.lambda$getUniqueSubscriptionDisplayNames$1(this.f$0, (SubscriptionInfo) obj);
    }
}

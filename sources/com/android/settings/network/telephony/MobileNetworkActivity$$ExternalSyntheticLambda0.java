package com.android.settings.network.telephony;

import com.android.settings.network.helper.SubscriptionAnnotation;
import java.util.function.Predicate;

public final /* synthetic */ class MobileNetworkActivity$$ExternalSyntheticLambda0 implements Predicate {
    public final /* synthetic */ MobileNetworkActivity f$0;

    public /* synthetic */ MobileNetworkActivity$$ExternalSyntheticLambda0(MobileNetworkActivity mobileNetworkActivity) {
        this.f$0 = mobileNetworkActivity;
    }

    public final boolean test(Object obj) {
        return this.f$0.lambda$getSubscription$0((SubscriptionAnnotation) obj);
    }
}

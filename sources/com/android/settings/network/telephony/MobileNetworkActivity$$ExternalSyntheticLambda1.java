package com.android.settings.network.telephony;

import com.android.settings.network.helper.SubscriptionAnnotation;
import java.util.function.Predicate;

public final /* synthetic */ class MobileNetworkActivity$$ExternalSyntheticLambda1 implements Predicate {
    public static final /* synthetic */ MobileNetworkActivity$$ExternalSyntheticLambda1 INSTANCE = new MobileNetworkActivity$$ExternalSyntheticLambda1();

    private /* synthetic */ MobileNetworkActivity$$ExternalSyntheticLambda1() {
    }

    public final boolean test(Object obj) {
        return ((SubscriptionAnnotation) obj).isActive();
    }
}

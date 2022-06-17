package com.android.settings.network;

import com.android.settings.network.helper.SubscriptionAnnotation;
import java.util.function.ToIntFunction;

public final /* synthetic */ class MobileNetworkSummaryController$$ExternalSyntheticLambda7 implements ToIntFunction {
    public static final /* synthetic */ MobileNetworkSummaryController$$ExternalSyntheticLambda7 INSTANCE = new MobileNetworkSummaryController$$ExternalSyntheticLambda7();

    private /* synthetic */ MobileNetworkSummaryController$$ExternalSyntheticLambda7() {
    }

    public final int applyAsInt(Object obj) {
        return ((SubscriptionAnnotation) obj).getSubscriptionId();
    }
}

package com.android.settings.network.helper;

import java.util.List;
import java.util.function.ToIntFunction;

public final /* synthetic */ class SubscriptionGrouping$$ExternalSyntheticLambda4 implements ToIntFunction {
    public final /* synthetic */ List f$0;

    public /* synthetic */ SubscriptionGrouping$$ExternalSyntheticLambda4(List list) {
        this.f$0 = list;
    }

    public final int applyAsInt(Object obj) {
        return this.f$0.indexOf((SubscriptionAnnotation) obj);
    }
}

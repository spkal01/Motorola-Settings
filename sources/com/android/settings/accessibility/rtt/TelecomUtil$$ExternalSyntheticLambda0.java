package com.android.settings.accessibility.rtt;

import android.telephony.SubscriptionInfo;
import java.util.function.Function;

public final /* synthetic */ class TelecomUtil$$ExternalSyntheticLambda0 implements Function {
    public static final /* synthetic */ TelecomUtil$$ExternalSyntheticLambda0 INSTANCE = new TelecomUtil$$ExternalSyntheticLambda0();

    private /* synthetic */ TelecomUtil$$ExternalSyntheticLambda0() {
    }

    public final Object apply(Object obj) {
        return Integer.valueOf(((SubscriptionInfo) obj).getSubscriptionId());
    }
}

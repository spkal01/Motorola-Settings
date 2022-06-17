package com.android.settings.network.helper;

import android.telephony.UiccSlotInfo;
import java.util.function.Predicate;

public final /* synthetic */ class QuerySimSlotIndex$$ExternalSyntheticLambda1 implements Predicate {
    public final /* synthetic */ QuerySimSlotIndex f$0;

    public /* synthetic */ QuerySimSlotIndex$$ExternalSyntheticLambda1(QuerySimSlotIndex querySimSlotIndex) {
        this.f$0 = querySimSlotIndex;
    }

    public final boolean test(Object obj) {
        return this.f$0.lambda$call$0((UiccSlotInfo) obj);
    }
}

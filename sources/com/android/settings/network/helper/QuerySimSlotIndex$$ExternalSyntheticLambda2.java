package com.android.settings.network.helper;

import android.telephony.UiccSlotInfo;
import java.util.function.ToIntFunction;

public final /* synthetic */ class QuerySimSlotIndex$$ExternalSyntheticLambda2 implements ToIntFunction {
    public final /* synthetic */ QuerySimSlotIndex f$0;

    public /* synthetic */ QuerySimSlotIndex$$ExternalSyntheticLambda2(QuerySimSlotIndex querySimSlotIndex) {
        this.f$0 = querySimSlotIndex;
    }

    public final int applyAsInt(Object obj) {
        return this.f$0.lambda$call$1((UiccSlotInfo) obj);
    }
}

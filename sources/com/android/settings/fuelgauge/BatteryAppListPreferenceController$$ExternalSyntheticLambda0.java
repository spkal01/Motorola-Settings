package com.android.settings.fuelgauge;

import android.os.UidBatteryConsumer;
import java.util.function.ToIntFunction;

public final /* synthetic */ class BatteryAppListPreferenceController$$ExternalSyntheticLambda0 implements ToIntFunction {
    public final /* synthetic */ BatteryAppListPreferenceController f$0;

    public /* synthetic */ BatteryAppListPreferenceController$$ExternalSyntheticLambda0(BatteryAppListPreferenceController batteryAppListPreferenceController) {
        this.f$0 = batteryAppListPreferenceController;
    }

    public final int applyAsInt(Object obj) {
        return this.f$0.lambda$getCoalescedUsageList$0((UidBatteryConsumer) obj);
    }
}

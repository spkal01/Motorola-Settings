package com.android.settings.fuelgauge;

import java.util.function.Consumer;

/* renamed from: com.android.settings.fuelgauge.BatteryChartPreferenceController$LoadAllItemsInfoTask$$ExternalSyntheticLambda1 */
public final /* synthetic */ class C0944xd2dd8c4 implements Consumer {
    public static final /* synthetic */ C0944xd2dd8c4 INSTANCE = new C0944xd2dd8c4();

    private /* synthetic */ C0944xd2dd8c4() {
    }

    public final void accept(Object obj) {
        ((BatteryDiffEntry) obj).loadLabelAndIcon();
    }
}

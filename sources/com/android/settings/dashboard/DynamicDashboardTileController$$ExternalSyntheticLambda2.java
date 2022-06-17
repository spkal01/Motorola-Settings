package com.android.settings.dashboard;

import java.util.function.Consumer;

public final /* synthetic */ class DynamicDashboardTileController$$ExternalSyntheticLambda2 implements Consumer {
    public static final /* synthetic */ DynamicDashboardTileController$$ExternalSyntheticLambda2 INSTANCE = new DynamicDashboardTileController$$ExternalSyntheticLambda2();

    private /* synthetic */ DynamicDashboardTileController$$ExternalSyntheticLambda2() {
    }

    public final void accept(Object obj) {
        ((DynamicDataObserver) obj).onDataChanged();
    }
}

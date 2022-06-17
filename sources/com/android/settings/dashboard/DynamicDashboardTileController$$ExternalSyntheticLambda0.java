package com.android.settings.dashboard;

import android.content.ContentResolver;
import java.util.function.Consumer;

public final /* synthetic */ class DynamicDashboardTileController$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ DynamicDashboardTileController f$0;
    public final /* synthetic */ ContentResolver f$1;

    public /* synthetic */ DynamicDashboardTileController$$ExternalSyntheticLambda0(DynamicDashboardTileController dynamicDashboardTileController, ContentResolver contentResolver) {
        this.f$0 = dynamicDashboardTileController;
        this.f$1 = contentResolver;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$registerDynamicDataObservers$1(this.f$1, (DynamicDataObserver) obj);
    }
}

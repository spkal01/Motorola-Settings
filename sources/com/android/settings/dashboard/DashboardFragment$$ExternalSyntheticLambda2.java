package com.android.settings.dashboard;

import android.content.ContentResolver;
import java.util.function.Consumer;

public final /* synthetic */ class DashboardFragment$$ExternalSyntheticLambda2 implements Consumer {
    public final /* synthetic */ ContentResolver f$0;

    public /* synthetic */ DashboardFragment$$ExternalSyntheticLambda2(ContentResolver contentResolver) {
        this.f$0 = contentResolver;
    }

    public final void accept(Object obj) {
        ((DynamicDashboardTileController) obj).onStop(this.f$0);
    }
}

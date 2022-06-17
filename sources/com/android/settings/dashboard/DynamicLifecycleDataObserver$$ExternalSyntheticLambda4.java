package com.android.settings.dashboard;

import android.os.Bundle;

public final /* synthetic */ class DynamicLifecycleDataObserver$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ DynamicLifecycleDataObserver f$0;
    public final /* synthetic */ Bundle f$1;

    public /* synthetic */ DynamicLifecycleDataObserver$$ExternalSyntheticLambda4(DynamicLifecycleDataObserver dynamicLifecycleDataObserver, Bundle bundle) {
        this.f$0 = dynamicLifecycleDataObserver;
        this.f$1 = bundle;
    }

    public final void run() {
        this.f$0.lambda$notifyLifecycleEvent$0(this.f$1);
    }
}

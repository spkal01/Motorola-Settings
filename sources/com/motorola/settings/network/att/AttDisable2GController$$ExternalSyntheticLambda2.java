package com.motorola.settings.network.att;

import androidx.lifecycle.Observer;

public final /* synthetic */ class AttDisable2GController$$ExternalSyntheticLambda2 implements Observer {
    public final /* synthetic */ AttDisable2GController f$0;

    public /* synthetic */ AttDisable2GController$$ExternalSyntheticLambda2(AttDisable2GController attDisable2GController) {
        this.f$0 = attDisable2GController;
    }

    public final void onChanged(Object obj) {
        this.f$0.onCallIdleStateChanged((Boolean) obj);
    }
}

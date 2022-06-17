package com.motorola.settings.network.att;

import androidx.lifecycle.Observer;

public final /* synthetic */ class Att5GSAModeController$$ExternalSyntheticLambda2 implements Observer {
    public final /* synthetic */ Att5GSAModeController f$0;

    public /* synthetic */ Att5GSAModeController$$ExternalSyntheticLambda2(Att5GSAModeController att5GSAModeController) {
        this.f$0 = att5GSAModeController;
    }

    public final void onChanged(Object obj) {
        this.f$0.onCallIdleStateChanged((Boolean) obj);
    }
}

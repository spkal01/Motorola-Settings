package com.motorola.settings.proxy.providers;

import com.android.settings.utils.SensorPrivacyManagerHelper;

public final /* synthetic */ class MuteUnmuteMic$$ExternalSyntheticLambda0 implements SensorPrivacyManagerHelper.Callback {
    public final /* synthetic */ MuteUnmuteMic f$0;

    public /* synthetic */ MuteUnmuteMic$$ExternalSyntheticLambda0(MuteUnmuteMic muteUnmuteMic) {
        this.f$0 = muteUnmuteMic;
    }

    public final void onSensorPrivacyChanged(int i, boolean z) {
        this.f$0.lambda$onCreate$0(i, z);
    }
}

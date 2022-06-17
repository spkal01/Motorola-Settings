package com.android.settings.network.telephony;

import java.util.function.Consumer;

public final /* synthetic */ class MobileNetworkSettings$$ExternalSyntheticLambda32 implements Consumer {
    public final /* synthetic */ MobileNetworkSettings f$0;
    public final /* synthetic */ VideoCallingPreferenceController f$1;

    public /* synthetic */ MobileNetworkSettings$$ExternalSyntheticLambda32(MobileNetworkSettings mobileNetworkSettings, VideoCallingPreferenceController videoCallingPreferenceController) {
        this.f$0 = mobileNetworkSettings;
        this.f$1 = videoCallingPreferenceController;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onAttach$32(this.f$1, (Enhanced4gCallingPreferenceController) obj);
    }
}

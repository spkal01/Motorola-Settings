package com.android.settings.network.telephony;

import java.util.function.Consumer;

public final /* synthetic */ class MobileNetworkSettings$$ExternalSyntheticLambda31 implements Consumer {
    public final /* synthetic */ MobileNetworkSettings f$0;
    public final /* synthetic */ VideoCallingPreferenceController f$1;

    public /* synthetic */ MobileNetworkSettings$$ExternalSyntheticLambda31(MobileNetworkSettings mobileNetworkSettings, VideoCallingPreferenceController videoCallingPreferenceController) {
        this.f$0 = mobileNetworkSettings;
        this.f$1 = videoCallingPreferenceController;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onAttach$33(this.f$1, (Enhanced4gAdvancedCallingPreferenceController) obj);
    }
}

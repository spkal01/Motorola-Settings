package com.motorola.settings.network.vzw;

import com.android.settings.network.telephony.VideoCallingPreferenceController;
import java.util.function.Consumer;

public final /* synthetic */ class VzwMobileNetworkSettings$$ExternalSyntheticLambda10 implements Consumer {
    public final /* synthetic */ VzwMobileNetworkSettings f$0;
    public final /* synthetic */ VideoCallingPreferenceController f$1;

    public /* synthetic */ VzwMobileNetworkSettings$$ExternalSyntheticLambda10(VzwMobileNetworkSettings vzwMobileNetworkSettings, VideoCallingPreferenceController videoCallingPreferenceController) {
        this.f$0 = vzwMobileNetworkSettings;
        this.f$1 = videoCallingPreferenceController;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onAttach$1(this.f$1, (VzwEnhanced4gLtePreferenceController) obj);
    }
}

package com.motorola.settings.network.vzw;

import com.android.settings.network.telephony.VideoCallingPreferenceController;
import java.util.function.Consumer;

public final /* synthetic */ class VzwMobileNetworkSettings$$ExternalSyntheticLambda8 implements Consumer {
    public final /* synthetic */ VzwMobileNetworkSettings f$0;
    public final /* synthetic */ VideoCallingPreferenceController f$1;

    public /* synthetic */ VzwMobileNetworkSettings$$ExternalSyntheticLambda8(VzwMobileNetworkSettings vzwMobileNetworkSettings, VideoCallingPreferenceController videoCallingPreferenceController) {
        this.f$0 = vzwMobileNetworkSettings;
        this.f$1 = videoCallingPreferenceController;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onAttach$3(this.f$1, (VzwAdvanceCallingPreferenceController) obj);
    }
}

package com.motorola.settings.network.sprint;

import com.android.settings.network.telephony.VideoCallingPreferenceController;
import java.util.function.Consumer;

public final /* synthetic */ class SprintMobileNetworkSettings$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ SprintMobileNetworkSettings f$0;
    public final /* synthetic */ VideoCallingPreferenceController f$1;

    public /* synthetic */ SprintMobileNetworkSettings$$ExternalSyntheticLambda1(SprintMobileNetworkSettings sprintMobileNetworkSettings, VideoCallingPreferenceController videoCallingPreferenceController) {
        this.f$0 = sprintMobileNetworkSettings;
        this.f$1 = videoCallingPreferenceController;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onAttach$2(this.f$1, (SprintAdvanceCallingPreferenceController) obj);
    }
}

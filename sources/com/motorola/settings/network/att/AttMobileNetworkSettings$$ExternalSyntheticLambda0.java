package com.motorola.settings.network.att;

import com.android.settings.network.telephony.CallingPreferenceCategoryController;
import com.android.settings.network.telephony.VideoCallingPreferenceController;
import com.android.settings.network.telephony.WifiCallingPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Arrays;
import java.util.function.Consumer;

public final /* synthetic */ class AttMobileNetworkSettings$$ExternalSyntheticLambda0 implements Consumer {
    public final /* synthetic */ WifiCallingPreferenceController f$0;
    public final /* synthetic */ VideoCallingPreferenceController f$1;
    public final /* synthetic */ AttWfcPreferenceController f$2;

    public /* synthetic */ AttMobileNetworkSettings$$ExternalSyntheticLambda0(WifiCallingPreferenceController wifiCallingPreferenceController, VideoCallingPreferenceController videoCallingPreferenceController, AttWfcPreferenceController attWfcPreferenceController) {
        this.f$0 = wifiCallingPreferenceController;
        this.f$1 = videoCallingPreferenceController;
        this.f$2 = attWfcPreferenceController;
    }

    public final void accept(Object obj) {
        ((CallingPreferenceCategoryController) obj).setChildren(Arrays.asList(new AbstractPreferenceController[]{this.f$0, this.f$1, this.f$2}));
    }
}

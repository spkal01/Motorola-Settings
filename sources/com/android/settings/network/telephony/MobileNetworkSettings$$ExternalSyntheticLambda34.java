package com.android.settings.network.telephony;

import com.android.settings.network.telephony.gsm.AutoSelectPreferenceController;
import com.android.settings.network.telephony.gsm.OpenNetworkSelectPagePreferenceController;
import java.util.function.Consumer;

public final /* synthetic */ class MobileNetworkSettings$$ExternalSyntheticLambda34 implements Consumer {
    public final /* synthetic */ MobileNetworkSettings f$0;
    public final /* synthetic */ OpenNetworkSelectPagePreferenceController f$1;

    public /* synthetic */ MobileNetworkSettings$$ExternalSyntheticLambda34(MobileNetworkSettings mobileNetworkSettings, OpenNetworkSelectPagePreferenceController openNetworkSelectPagePreferenceController) {
        this.f$0 = mobileNetworkSettings;
        this.f$1 = openNetworkSelectPagePreferenceController;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onAttach$23(this.f$1, (AutoSelectPreferenceController) obj);
    }
}

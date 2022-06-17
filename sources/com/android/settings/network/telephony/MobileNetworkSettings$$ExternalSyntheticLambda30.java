package com.android.settings.network.telephony;

import android.content.Context;
import java.util.function.Consumer;

public final /* synthetic */ class MobileNetworkSettings$$ExternalSyntheticLambda30 implements Consumer {
    public final /* synthetic */ MobileNetworkSettings f$0;
    public final /* synthetic */ Context f$1;

    public /* synthetic */ MobileNetworkSettings$$ExternalSyntheticLambda30(MobileNetworkSettings mobileNetworkSettings, Context context) {
        this.f$0 = mobileNetworkSettings;
        this.f$1 = context;
    }

    public final void accept(Object obj) {
        this.f$0.lambda$onAttach$12(this.f$1, (MobileDataPreferenceController) obj);
    }
}

package com.motorola.settings.network.tmo;

import com.android.settings.network.telephony.CallingPreferenceCategoryController;
import java.util.List;
import java.util.function.Consumer;

public final /* synthetic */ class TmoMobileNetworkSettings$$ExternalSyntheticLambda4 implements Consumer {
    public final /* synthetic */ List f$0;

    public /* synthetic */ TmoMobileNetworkSettings$$ExternalSyntheticLambda4(List list) {
        this.f$0 = list;
    }

    public final void accept(Object obj) {
        ((CallingPreferenceCategoryController) obj).setChildren(this.f$0);
    }
}

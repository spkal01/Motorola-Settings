package com.android.settings.network;

import com.android.settings.network.ProxySubscriptionManager;
import java.util.function.Consumer;

public final /* synthetic */ class ProxySubscriptionManager$$ExternalSyntheticLambda2 implements Consumer {
    public static final /* synthetic */ ProxySubscriptionManager$$ExternalSyntheticLambda2 INSTANCE = new ProxySubscriptionManager$$ExternalSyntheticLambda2();

    private /* synthetic */ ProxySubscriptionManager$$ExternalSyntheticLambda2() {
    }

    public final void accept(Object obj) {
        ((ProxySubscriptionManager.OnActiveSubscriptionChangedListener) obj).onChanged();
    }
}

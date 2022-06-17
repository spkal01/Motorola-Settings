package com.android.settings.network;

import com.android.settings.network.ProxySubscriptionManager;
import java.util.function.Function;

public final /* synthetic */ class ProxySubscriptionManager$$ExternalSyntheticLambda3 implements Function {
    public final /* synthetic */ ProxySubscriptionManager f$0;

    public /* synthetic */ ProxySubscriptionManager$$ExternalSyntheticLambda3(ProxySubscriptionManager proxySubscriptionManager) {
        this.f$0 = proxySubscriptionManager;
    }

    public final Object apply(Object obj) {
        return this.f$0.lambda$processStatusChangeOnListeners$1((ProxySubscriptionManager.OnActiveSubscriptionChangedListener) obj);
    }
}

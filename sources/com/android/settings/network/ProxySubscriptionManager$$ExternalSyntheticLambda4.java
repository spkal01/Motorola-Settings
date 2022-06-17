package com.android.settings.network;

import com.android.settings.network.ProxySubscriptionManager;
import java.util.function.Predicate;

public final /* synthetic */ class ProxySubscriptionManager$$ExternalSyntheticLambda4 implements Predicate {
    public final /* synthetic */ ProxySubscriptionManager f$0;
    public final /* synthetic */ ProxySubscriptionManager.OnActiveSubscriptionChangedListener f$1;

    public /* synthetic */ ProxySubscriptionManager$$ExternalSyntheticLambda4(ProxySubscriptionManager proxySubscriptionManager, ProxySubscriptionManager.OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener) {
        this.f$0 = proxySubscriptionManager;
        this.f$1 = onActiveSubscriptionChangedListener;
    }

    public final boolean test(Object obj) {
        return this.f$0.lambda$removeSpecificListenerAndCleanList$0(this.f$1, (ProxySubscriptionManager.OnActiveSubscriptionChangedListener) obj);
    }
}

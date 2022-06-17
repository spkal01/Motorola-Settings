package com.motorola.settings.network.telephony;

import android.telephony.SubscriptionInfo;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

public final /* synthetic */ class ExtendedProviderModelSliceHelper$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ExtendedProviderModelSliceHelper f$0;
    public final /* synthetic */ AtomicReference f$1;
    public final /* synthetic */ SubscriptionInfo f$2;
    public final /* synthetic */ Semaphore f$3;

    public /* synthetic */ ExtendedProviderModelSliceHelper$$ExternalSyntheticLambda1(ExtendedProviderModelSliceHelper extendedProviderModelSliceHelper, AtomicReference atomicReference, SubscriptionInfo subscriptionInfo, Semaphore semaphore) {
        this.f$0 = extendedProviderModelSliceHelper;
        this.f$1 = atomicReference;
        this.f$2 = subscriptionInfo;
        this.f$3 = semaphore;
    }

    public final void run() {
        this.f$0.lambda$getMobileRowDrawable$0(this.f$1, this.f$2, this.f$3);
    }
}

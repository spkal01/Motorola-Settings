package com.android.settings.network;

import android.content.Context;
import java.util.concurrent.Callable;

public final /* synthetic */ class MobileNetworkSummaryStatus$$ExternalSyntheticLambda0 implements Callable {
    public final /* synthetic */ MobileNetworkSummaryStatus f$0;
    public final /* synthetic */ Context f$1;

    public /* synthetic */ MobileNetworkSummaryStatus$$ExternalSyntheticLambda0(MobileNetworkSummaryStatus mobileNetworkSummaryStatus, Context context) {
        this.f$0 = mobileNetworkSummaryStatus;
        this.f$1 = context;
    }

    public final Object call() {
        return this.f$0.lambda$update$2(this.f$1);
    }
}

package com.android.settings.network.helper;

import com.android.settings.network.helper.SubscriptionAnnotation;
import java.util.List;
import java.util.function.Function;

public final /* synthetic */ class SelectableSubscriptions$$ExternalSyntheticLambda0 implements Function {
    public final /* synthetic */ SelectableSubscriptions f$0;
    public final /* synthetic */ List f$1;
    public final /* synthetic */ List f$2;
    public final /* synthetic */ List f$3;

    public /* synthetic */ SelectableSubscriptions$$ExternalSyntheticLambda0(SelectableSubscriptions selectableSubscriptions, List list, List list2, List list3) {
        this.f$0 = selectableSubscriptions;
        this.f$1 = list;
        this.f$2 = list2;
        this.f$3 = list3;
    }

    public final Object apply(Object obj) {
        return this.f$0.lambda$call$6(this.f$1, this.f$2, this.f$3, (SubscriptionAnnotation.Builder) obj);
    }
}

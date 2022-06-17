package com.android.settings.network.helper;

import java.util.function.Predicate;

public final /* synthetic */ class SelectableSubscriptions$$ExternalSyntheticLambda6 implements Predicate {
    public static final /* synthetic */ SelectableSubscriptions$$ExternalSyntheticLambda6 INSTANCE = new SelectableSubscriptions$$ExternalSyntheticLambda6();

    private /* synthetic */ SelectableSubscriptions$$ExternalSyntheticLambda6() {
    }

    public final boolean test(Object obj) {
        return ((SubscriptionAnnotation) obj).isActive();
    }
}

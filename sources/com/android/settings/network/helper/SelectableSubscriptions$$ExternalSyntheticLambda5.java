package com.android.settings.network.helper;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.IntUnaryOperator;

public final /* synthetic */ class SelectableSubscriptions$$ExternalSyntheticLambda5 implements IntUnaryOperator {
    public final /* synthetic */ AtomicIntegerArray f$0;

    public /* synthetic */ SelectableSubscriptions$$ExternalSyntheticLambda5(AtomicIntegerArray atomicIntegerArray) {
        this.f$0 = atomicIntegerArray;
    }

    public final int applyAsInt(int i) {
        return this.f$0.get(i);
    }
}

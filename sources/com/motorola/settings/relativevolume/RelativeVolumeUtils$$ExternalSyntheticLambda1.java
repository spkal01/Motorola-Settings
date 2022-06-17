package com.motorola.settings.relativevolume;

import android.content.pm.ApplicationInfo;
import java.util.function.Predicate;

public final /* synthetic */ class RelativeVolumeUtils$$ExternalSyntheticLambda1 implements Predicate {
    public final /* synthetic */ int f$0;

    public /* synthetic */ RelativeVolumeUtils$$ExternalSyntheticLambda1(int i) {
        this.f$0 = i;
    }

    public final boolean test(Object obj) {
        return RelativeVolumeUtils.lambda$getApplicationForUid$0(this.f$0, (ApplicationInfo) obj);
    }
}

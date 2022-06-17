package com.android.settingslib.utils;

import android.os.UserHandle;
import java.util.function.Predicate;

public final /* synthetic */ class AppCloneUtils$$ExternalSyntheticLambda0 implements Predicate {
    public static final /* synthetic */ AppCloneUtils$$ExternalSyntheticLambda0 INSTANCE = new AppCloneUtils$$ExternalSyntheticLambda0();

    private /* synthetic */ AppCloneUtils$$ExternalSyntheticLambda0() {
    }

    public final boolean test(Object obj) {
        return AppCloneUtils.lambda$getUserProfileExceptClone$0((UserHandle) obj);
    }
}

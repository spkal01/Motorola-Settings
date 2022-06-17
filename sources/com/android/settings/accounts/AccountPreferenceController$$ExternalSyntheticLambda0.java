package com.android.settings.accounts;

import android.content.pm.UserInfo;
import java.util.function.Predicate;

public final /* synthetic */ class AccountPreferenceController$$ExternalSyntheticLambda0 implements Predicate {
    public static final /* synthetic */ AccountPreferenceController$$ExternalSyntheticLambda0 INSTANCE = new AccountPreferenceController$$ExternalSyntheticLambda0();

    private /* synthetic */ AccountPreferenceController$$ExternalSyntheticLambda0() {
    }

    public final boolean test(Object obj) {
        return AccountPreferenceController.lambda$isSingleProfile$0((UserInfo) obj);
    }
}

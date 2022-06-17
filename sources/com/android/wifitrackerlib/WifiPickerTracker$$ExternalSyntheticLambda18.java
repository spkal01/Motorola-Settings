package com.android.wifitrackerlib;

import java.util.List;
import java.util.function.Predicate;

public final /* synthetic */ class WifiPickerTracker$$ExternalSyntheticLambda18 implements Predicate {
    public final /* synthetic */ List f$0;

    public /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda18(List list) {
        this.f$0 = list;
    }

    public final boolean test(Object obj) {
        return WifiPickerTracker.lambda$applyAdminRestrictions$1(this.f$0, (WifiEntry) obj);
    }
}

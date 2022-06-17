package com.android.settings.wifi;

import com.android.wifitrackerlib.WifiEntry;
import java.util.function.Predicate;

public final /* synthetic */ class WifiSettings$$ExternalSyntheticLambda11 implements Predicate {
    public static final /* synthetic */ WifiSettings$$ExternalSyntheticLambda11 INSTANCE = new WifiSettings$$ExternalSyntheticLambda11();

    private /* synthetic */ WifiSettings$$ExternalSyntheticLambda11() {
    }

    public final boolean test(Object obj) {
        return WifiSettings.lambda$onWifiEntriesChanged$6((WifiEntry) obj);
    }
}

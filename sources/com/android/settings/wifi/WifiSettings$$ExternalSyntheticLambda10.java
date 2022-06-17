package com.android.settings.wifi;

import com.android.wifitrackerlib.WifiEntry;
import java.util.function.Predicate;

public final /* synthetic */ class WifiSettings$$ExternalSyntheticLambda10 implements Predicate {
    public static final /* synthetic */ WifiSettings$$ExternalSyntheticLambda10 INSTANCE = new WifiSettings$$ExternalSyntheticLambda10();

    private /* synthetic */ WifiSettings$$ExternalSyntheticLambda10() {
    }

    public final boolean test(Object obj) {
        return WifiSettings.lambda$onWifiEntriesChanged$7((WifiEntry) obj);
    }
}

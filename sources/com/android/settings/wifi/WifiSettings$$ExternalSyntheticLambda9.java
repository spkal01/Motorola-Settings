package com.android.settings.wifi;

import com.android.wifitrackerlib.WifiEntry;
import java.util.function.Predicate;

public final /* synthetic */ class WifiSettings$$ExternalSyntheticLambda9 implements Predicate {
    public final /* synthetic */ WifiSettings f$0;

    public /* synthetic */ WifiSettings$$ExternalSyntheticLambda9(WifiSettings wifiSettings) {
        this.f$0 = wifiSettings;
    }

    public final boolean test(Object obj) {
        return this.f$0.lambda$onWifiEntriesChanged$5((WifiEntry) obj);
    }
}

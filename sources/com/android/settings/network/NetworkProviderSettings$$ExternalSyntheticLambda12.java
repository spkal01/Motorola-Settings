package com.android.settings.network;

import com.android.wifitrackerlib.WifiEntry;
import java.util.function.Predicate;

public final /* synthetic */ class NetworkProviderSettings$$ExternalSyntheticLambda12 implements Predicate {
    public static final /* synthetic */ NetworkProviderSettings$$ExternalSyntheticLambda12 INSTANCE = new NetworkProviderSettings$$ExternalSyntheticLambda12();

    private /* synthetic */ NetworkProviderSettings$$ExternalSyntheticLambda12() {
    }

    public final boolean test(Object obj) {
        return NetworkProviderSettings.lambda$onWifiEntriesChanged$8((WifiEntry) obj);
    }
}

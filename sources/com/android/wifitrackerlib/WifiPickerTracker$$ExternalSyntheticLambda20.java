package com.android.wifitrackerlib;

import android.net.wifi.ScanResult;
import java.util.function.Predicate;

public final /* synthetic */ class WifiPickerTracker$$ExternalSyntheticLambda20 implements Predicate {
    public static final /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda20 INSTANCE = new WifiPickerTracker$$ExternalSyntheticLambda20();

    private /* synthetic */ WifiPickerTracker$$ExternalSyntheticLambda20() {
    }

    public final boolean test(Object obj) {
        return WifiPickerTracker.lambda$updateSuggestedWifiEntryScans$15((ScanResult) obj);
    }
}

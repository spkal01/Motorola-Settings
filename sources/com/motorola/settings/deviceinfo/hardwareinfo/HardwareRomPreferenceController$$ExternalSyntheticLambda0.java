package com.motorola.settings.deviceinfo.hardwareinfo;

import android.app.usage.StorageStatsManager;
import androidx.preference.Preference;

public final /* synthetic */ class HardwareRomPreferenceController$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ HardwareRomPreferenceController f$0;
    public final /* synthetic */ StorageStatsManager f$1;
    public final /* synthetic */ Preference f$2;

    public /* synthetic */ HardwareRomPreferenceController$$ExternalSyntheticLambda0(HardwareRomPreferenceController hardwareRomPreferenceController, StorageStatsManager storageStatsManager, Preference preference) {
        this.f$0 = hardwareRomPreferenceController;
        this.f$1 = storageStatsManager;
        this.f$2 = preference;
    }

    public final void run() {
        this.f$0.lambda$updateState$1(this.f$1, this.f$2);
    }
}

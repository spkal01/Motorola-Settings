package com.motorola.settings.deviceinfo.hardwareinfo;

import androidx.preference.Preference;

public final /* synthetic */ class HardwareRamPreferenceController$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ HardwareRamPreferenceController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ Preference f$2;

    public /* synthetic */ HardwareRamPreferenceController$$ExternalSyntheticLambda0(HardwareRamPreferenceController hardwareRamPreferenceController, long j, Preference preference) {
        this.f$0 = hardwareRamPreferenceController;
        this.f$1 = j;
        this.f$2 = preference;
    }

    public final void run() {
        this.f$0.lambda$updateState$0(this.f$1, this.f$2);
    }
}

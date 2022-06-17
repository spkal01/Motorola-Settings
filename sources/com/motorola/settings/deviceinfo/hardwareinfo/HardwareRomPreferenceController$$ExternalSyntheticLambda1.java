package com.motorola.settings.deviceinfo.hardwareinfo;

import androidx.preference.Preference;

public final /* synthetic */ class HardwareRomPreferenceController$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ HardwareRomPreferenceController f$0;
    public final /* synthetic */ Preference f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ HardwareRomPreferenceController$$ExternalSyntheticLambda1(HardwareRomPreferenceController hardwareRomPreferenceController, Preference preference, long j) {
        this.f$0 = hardwareRomPreferenceController;
        this.f$1 = preference;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$updateState$0(this.f$1, this.f$2);
    }
}

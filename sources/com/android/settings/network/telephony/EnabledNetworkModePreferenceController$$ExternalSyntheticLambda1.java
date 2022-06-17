package com.android.settings.network.telephony;

public final /* synthetic */ class EnabledNetworkModePreferenceController$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ EnabledNetworkModePreferenceController f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ EnabledNetworkModePreferenceController$$ExternalSyntheticLambda1(EnabledNetworkModePreferenceController enabledNetworkModePreferenceController, int i) {
        this.f$0 = enabledNetworkModePreferenceController;
        this.f$1 = i;
    }

    public final void run() {
        this.f$0.lambda$setPreferredNetworkTypeBitmask$1(this.f$1);
    }
}

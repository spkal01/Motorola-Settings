package com.android.settings.network.telephony;

import com.android.settingslib.core.AbstractPreferenceController;
import java.util.Arrays;
import java.util.function.Consumer;

public final /* synthetic */ class MobileNetworkSettings$$ExternalSyntheticLambda35 implements Consumer {
    public final /* synthetic */ WifiCallingPreferenceController f$0;
    public final /* synthetic */ VideoCallingPreferenceController f$1;
    public final /* synthetic */ BackupCallingPreferenceController f$2;

    public /* synthetic */ MobileNetworkSettings$$ExternalSyntheticLambda35(WifiCallingPreferenceController wifiCallingPreferenceController, VideoCallingPreferenceController videoCallingPreferenceController, BackupCallingPreferenceController backupCallingPreferenceController) {
        this.f$0 = wifiCallingPreferenceController;
        this.f$1 = videoCallingPreferenceController;
        this.f$2 = backupCallingPreferenceController;
    }

    public final void accept(Object obj) {
        ((CallingPreferenceCategoryController) obj).setChildren(Arrays.asList(new AbstractPreferenceController[]{this.f$0, this.f$1, this.f$2}));
    }
}

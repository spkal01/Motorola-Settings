package com.android.settings.wifi;

import android.content.DialogInterface;
import com.android.wifitrackerlib.WifiEntry;

public final /* synthetic */ class WifiSettings$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ WifiSettings f$0;
    public final /* synthetic */ WifiEntry f$1;

    public /* synthetic */ WifiSettings$$ExternalSyntheticLambda1(WifiSettings wifiSettings, WifiEntry wifiEntry) {
        this.f$0 = wifiSettings;
        this.f$1 = wifiEntry;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onPreferenceTreeClick$4(this.f$1, dialogInterface, i);
    }
}

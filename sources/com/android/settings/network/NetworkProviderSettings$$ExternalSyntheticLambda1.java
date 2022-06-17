package com.android.settings.network;

import android.content.DialogInterface;
import com.android.wifitrackerlib.WifiEntry;

public final /* synthetic */ class NetworkProviderSettings$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ NetworkProviderSettings f$0;
    public final /* synthetic */ WifiEntry f$1;

    public /* synthetic */ NetworkProviderSettings$$ExternalSyntheticLambda1(NetworkProviderSettings networkProviderSettings, WifiEntry wifiEntry) {
        this.f$0 = networkProviderSettings;
        this.f$1 = wifiEntry;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onPreferenceTreeClick$5(this.f$1, dialogInterface, i);
    }
}

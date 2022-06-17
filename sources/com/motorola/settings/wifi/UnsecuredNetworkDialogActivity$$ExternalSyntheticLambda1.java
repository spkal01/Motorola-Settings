package com.motorola.settings.wifi;

import android.content.DialogInterface;
import com.android.wifitrackerlib.WifiEntry;

public final /* synthetic */ class UnsecuredNetworkDialogActivity$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ UnsecuredNetworkDialogActivity f$0;
    public final /* synthetic */ WifiEntry f$1;

    public /* synthetic */ UnsecuredNetworkDialogActivity$$ExternalSyntheticLambda1(UnsecuredNetworkDialogActivity unsecuredNetworkDialogActivity, WifiEntry wifiEntry) {
        this.f$0 = unsecuredNetworkDialogActivity;
        this.f$1 = wifiEntry;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onCreate$0(this.f$1, dialogInterface, i);
    }
}

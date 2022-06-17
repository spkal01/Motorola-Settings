package com.android.settings.wifi.dpp;

import android.content.DialogInterface;
import com.android.settings.wifi.dpp.WifiDppQrCodeScannerFragment;

public final /* synthetic */ class WifiDppQrCodeScannerFragment$1$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ WifiDppQrCodeScannerFragment.C14361 f$0;
    public final /* synthetic */ WifiNetworkConfig f$1;

    public /* synthetic */ WifiDppQrCodeScannerFragment$1$$ExternalSyntheticLambda1(WifiDppQrCodeScannerFragment.C14361 r1, WifiNetworkConfig wifiNetworkConfig) {
        this.f$0 = r1;
        this.f$1 = wifiNetworkConfig;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$handleMessage$0(this.f$1, dialogInterface, i);
    }
}

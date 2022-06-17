package com.android.settings.wifi.tether;

import android.content.DialogInterface;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;

public final /* synthetic */ class WifiTetherBasePreferenceController$1$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ WifiTetherBasePreferenceController.C14581 f$0;
    public final /* synthetic */ Object f$1;

    public /* synthetic */ WifiTetherBasePreferenceController$1$$ExternalSyntheticLambda0(WifiTetherBasePreferenceController.C14581 r1, Object obj) {
        this.f$0 = r1;
        this.f$1 = obj;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onPreferenceChange$0(this.f$1, dialogInterface, i);
    }
}

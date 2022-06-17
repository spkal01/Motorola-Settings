package com.motorola.settings.tether;

import android.content.DialogInterface;
import android.widget.CheckBox;

public final /* synthetic */ class WifiTetherStartupDialog$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ WifiTetherStartupDialog f$0;
    public final /* synthetic */ CheckBox f$1;

    public /* synthetic */ WifiTetherStartupDialog$$ExternalSyntheticLambda1(WifiTetherStartupDialog wifiTetherStartupDialog, CheckBox checkBox) {
        this.f$0 = wifiTetherStartupDialog;
        this.f$1 = checkBox;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onCreateDialog$0(this.f$1, dialogInterface, i);
    }
}

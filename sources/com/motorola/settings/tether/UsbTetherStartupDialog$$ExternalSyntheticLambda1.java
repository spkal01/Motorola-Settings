package com.motorola.settings.tether;

import android.content.DialogInterface;
import android.widget.CheckBox;

public final /* synthetic */ class UsbTetherStartupDialog$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ UsbTetherStartupDialog f$0;
    public final /* synthetic */ CheckBox f$1;

    public /* synthetic */ UsbTetherStartupDialog$$ExternalSyntheticLambda1(UsbTetherStartupDialog usbTetherStartupDialog, CheckBox checkBox) {
        this.f$0 = usbTetherStartupDialog;
        this.f$1 = checkBox;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onCreateDialog$1(this.f$1, dialogInterface, i);
    }
}

package com.motorola.settings.tether;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.CheckBox;

public final /* synthetic */ class WifiHotspotConflictWarningDialog$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ WifiHotspotConflictWarningDialog f$0;
    public final /* synthetic */ CheckBox f$1;
    public final /* synthetic */ Context f$2;

    public /* synthetic */ WifiHotspotConflictWarningDialog$$ExternalSyntheticLambda1(WifiHotspotConflictWarningDialog wifiHotspotConflictWarningDialog, CheckBox checkBox, Context context) {
        this.f$0 = wifiHotspotConflictWarningDialog;
        this.f$1 = checkBox;
        this.f$2 = context;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onCreateDialog$1(this.f$1, this.f$2, dialogInterface, i);
    }
}

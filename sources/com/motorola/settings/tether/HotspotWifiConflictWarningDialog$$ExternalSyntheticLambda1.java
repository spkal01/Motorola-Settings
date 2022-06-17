package com.motorola.settings.tether;

import android.content.DialogInterface;
import android.widget.CheckBox;

public final /* synthetic */ class HotspotWifiConflictWarningDialog$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ HotspotWifiConflictWarningDialog f$0;
    public final /* synthetic */ CheckBox f$1;

    public /* synthetic */ HotspotWifiConflictWarningDialog$$ExternalSyntheticLambda1(HotspotWifiConflictWarningDialog hotspotWifiConflictWarningDialog, CheckBox checkBox) {
        this.f$0 = hotspotWifiConflictWarningDialog;
        this.f$1 = checkBox;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onCreateDialog$0(this.f$1, dialogInterface, i);
    }
}

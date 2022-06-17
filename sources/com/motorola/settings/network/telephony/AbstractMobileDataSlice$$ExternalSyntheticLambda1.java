package com.motorola.settings.network.telephony;

import android.content.DialogInterface;

public final /* synthetic */ class AbstractMobileDataSlice$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ AbstractMobileDataSlice f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ AbstractMobileDataSlice$$ExternalSyntheticLambda1(AbstractMobileDataSlice abstractMobileDataSlice, int i) {
        this.f$0 = abstractMobileDataSlice;
        this.f$1 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onBeforeToggleAction$0(this.f$1, dialogInterface, i);
    }
}

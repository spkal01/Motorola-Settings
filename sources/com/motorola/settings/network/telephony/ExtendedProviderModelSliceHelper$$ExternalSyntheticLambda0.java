package com.motorola.settings.network.telephony;

import android.content.DialogInterface;

public final /* synthetic */ class ExtendedProviderModelSliceHelper$$ExternalSyntheticLambda0 implements DialogInterface.OnCancelListener {
    public final /* synthetic */ DialogInterface.OnClickListener f$0;

    public /* synthetic */ ExtendedProviderModelSliceHelper$$ExternalSyntheticLambda0(DialogInterface.OnClickListener onClickListener) {
        this.f$0 = onClickListener;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        ExtendedProviderModelSliceHelper.lambda$getMobileDataDisableDialog$1(this.f$0, dialogInterface);
    }
}

package com.android.settingslib.enterprise;

import android.content.Context;
import android.content.DialogInterface;
import com.android.settingslib.RestrictedLockUtils;

/* renamed from: com.android.settingslib.enterprise.BiometricActionDisabledByAdminController$$ExternalSyntheticLambda0 */
public final /* synthetic */ class C1529xfa36ded9 implements DialogInterface.OnClickListener {
    public final /* synthetic */ RestrictedLockUtils.EnforcedAdmin f$0;
    public final /* synthetic */ Context f$1;

    public /* synthetic */ C1529xfa36ded9(RestrictedLockUtils.EnforcedAdmin enforcedAdmin, Context context) {
        this.f$0 = enforcedAdmin;
        this.f$1 = context;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        BiometricActionDisabledByAdminController.lambda$getPositiveButtonListener$0(this.f$0, this.f$1, dialogInterface, i);
    }
}

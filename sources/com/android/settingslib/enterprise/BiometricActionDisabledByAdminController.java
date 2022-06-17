package com.android.settingslib.enterprise;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import com.android.settingslib.RestrictedLockUtils;

public class BiometricActionDisabledByAdminController extends BaseActionDisabledByAdminController {
    public void setupLearnMoreButton(Context context) {
    }

    BiometricActionDisabledByAdminController(DeviceAdminStringProvider deviceAdminStringProvider) {
        super(deviceAdminStringProvider);
    }

    public String getAdminSupportTitle(String str) {
        return this.mStringProvider.getDisabledBiometricsParentConsentTitle();
    }

    public CharSequence getAdminSupportContentString(Context context, CharSequence charSequence) {
        return this.mStringProvider.getDisabledBiometricsParentConsentContent();
    }

    public DialogInterface.OnClickListener getPositiveButtonListener(Context context, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        return new C1529xfa36ded9(enforcedAdmin, context);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$getPositiveButtonListener$0(RestrictedLockUtils.EnforcedAdmin enforcedAdmin, Context context, DialogInterface dialogInterface, int i) {
        Log.d("BiometricActionDisabledByAdminController", "Positive button clicked, component: " + enforcedAdmin.component);
        context.startActivity(new Intent("android.intent.action.MANAGE_RESTRICTED_SETTING").putExtra("extra_setting", "biometric_disabled_by_admin_controller").setPackage(enforcedAdmin.component.getPackageName()));
    }
}

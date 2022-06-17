package com.android.settings.biometrics.fingerprint;

import android.app.Activity;
import android.view.View;
import com.android.settingslib.RestrictedLockUtils;

/* renamed from: com.android.settings.biometrics.fingerprint.FingerprintSettings$FingerprintSettingsFragment$$ExternalSyntheticLambda1 */
public final /* synthetic */ class C0786xd2803eaa implements View.OnClickListener {
    public final /* synthetic */ Activity f$0;
    public final /* synthetic */ RestrictedLockUtils.EnforcedAdmin f$1;

    public /* synthetic */ C0786xd2803eaa(Activity activity, RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.f$0 = activity;
        this.f$1 = enforcedAdmin;
    }

    public final void onClick(View view) {
        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.f$0, this.f$1);
    }
}

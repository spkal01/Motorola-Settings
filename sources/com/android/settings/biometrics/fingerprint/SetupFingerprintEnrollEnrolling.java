package com.android.settings.biometrics.fingerprint;

import android.content.Intent;
import com.android.settings.SetupWizardUtils;
import com.motorola.settings.biometrics.fingerprint.FingerprintUtils;
import com.motorola.settings.biometrics.fingerprint.SetupFingerprintEnrollAlmostThere;

public class SetupFingerprintEnrollEnrolling extends FingerprintEnrollEnrolling {
    public int getMetricsCategory() {
        return 246;
    }

    /* access modifiers changed from: protected */
    public Intent getFinishIntent() {
        Intent intent;
        if (FingerprintUtils.shouldShowTouchToUnlockSettings(this, false, false, true)) {
            intent = new Intent(this, SetupFingerprintEnrollAlmostThere.class);
        } else {
            intent = new Intent(this, SetupFingerprintEnrollFinish.class);
        }
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        return intent;
    }
}

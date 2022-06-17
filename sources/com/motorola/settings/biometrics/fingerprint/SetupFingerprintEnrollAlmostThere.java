package com.motorola.settings.biometrics.fingerprint;

import android.content.Intent;
import com.android.settings.SetupWizardUtils;
import com.android.settings.biometrics.fingerprint.SetupFingerprintEnrollFinish;

public class SetupFingerprintEnrollAlmostThere extends FingerprintEnrollAlmostThere {
    public int getMetricsCategory() {
        return 248;
    }

    /* access modifiers changed from: protected */
    public Intent getFinishIntent() {
        Intent intent = new Intent(this, SetupFingerprintEnrollFinish.class);
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        return intent;
    }
}

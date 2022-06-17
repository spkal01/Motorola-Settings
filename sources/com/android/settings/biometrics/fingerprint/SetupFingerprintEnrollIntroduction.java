package com.android.settings.biometrics.fingerprint;

import android.app.KeyguardManager;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C1992R$string;
import com.android.settings.SetupWizardUtils;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricUtils;

public class SetupFingerprintEnrollIntroduction extends FingerprintEnrollIntroduction {
    private boolean mAlreadyHadLockScreenSetup = false;

    public int getMetricsCategory() {
        return 249;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            this.mAlreadyHadLockScreenSetup = isKeyguardSecure();
        } else {
            this.mAlreadyHadLockScreenSetup = bundle.getBoolean("wasLockScreenPresent", false);
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("wasLockScreenPresent", this.mAlreadyHadLockScreenSetup);
    }

    /* access modifiers changed from: protected */
    public Intent getEnrollingIntent() {
        Intent intent = new Intent(this, SetupFingerprintEnrollFindSensor.class);
        if (BiometricUtils.containsGatekeeperPasswordHandle(getIntent())) {
            intent.putExtra("gk_pw_handle", BiometricUtils.getGatekeeperPasswordHandle(getIntent()));
        }
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        return intent;
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 2 && isKeyguardSecure()) {
            if (!this.mAlreadyHadLockScreenSetup) {
                intent = getMetricIntent(intent);
            }
            if (i2 == 1) {
                intent = setFingerprintCount(intent);
            }
        }
        super.onActivityResult(i, i2, intent);
    }

    private Intent getMetricIntent(Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.putExtra(":settings:password_quality", new LockPatternUtils(this).getKeyguardStoredPasswordQuality(UserHandle.myUserId()));
        return intent;
    }

    private Intent setFingerprintCount(Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        if (fingerprintManagerOrNull != null) {
            intent.putExtra("fingerprint_enrolled_count", fingerprintManagerOrNull.getEnrolledFingerprints(this.mUserId).size());
        }
        return intent;
    }

    /* access modifiers changed from: protected */
    public void onCancelButtonClick(View view) {
        int i;
        Intent intent = null;
        if (isKeyguardSecure()) {
            i = 2;
            if (!this.mAlreadyHadLockScreenSetup) {
                intent = getMetricIntent((Intent) null);
            }
        } else {
            i = 11;
        }
        setResult(i, FingerprintEnrollIntroduction.setSkipPendingEnroll(intent));
        finish();
    }

    public void onBackPressed() {
        if (!this.mAlreadyHadLockScreenSetup && isKeyguardSecure()) {
            setResult(0, getMetricIntent((Intent) null));
        }
        super.onBackPressed();
    }

    private boolean isKeyguardSecure() {
        return ((KeyguardManager) getSystemService(KeyguardManager.class)).isKeyguardSecure();
    }

    /* access modifiers changed from: package-private */
    public int getNegativeButtonTextId() {
        return C1992R$string.security_fingerprint_skip;
    }
}

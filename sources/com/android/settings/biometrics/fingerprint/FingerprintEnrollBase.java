package com.android.settings.biometrics.fingerprint;

import android.content.res.Resources;
import android.os.Bundle;
import com.android.settings.C1981R$color;
import com.android.settings.biometrics.BiometricEnrollBase;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.motorola.settings.biometrics.fingerprint.FingerprintUtils;

public abstract class FingerprintEnrollBase extends BiometricEnrollBase {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getIntent().putExtra(WizardManagerHelper.EXTRA_ALLOW_CUSTOMIZATION_IN_SETUP_FLOW, true);
    }

    /* access modifiers changed from: protected */
    public boolean needsFODTheme() {
        return FingerprintUtils.isMotoFODEnabled(this);
    }

    /* access modifiers changed from: protected */
    public void initViews() {
        FingerprintUtils.overrideFpEnrollSuwHeaderTitleSizeIfNecessary(this);
        if (needsFODTheme()) {
            FingerprintUtils.applyFODTheme(this);
        }
    }

    /* access modifiers changed from: protected */
    public int getBackgroundColor() {
        if (needsFODTheme()) {
            return getResources().getColor(C1981R$color.fod_fingerprint_background_color, (Resources.Theme) null);
        }
        return super.getBackgroundColor();
    }
}

package com.motorola.settings.biometrics.fingerprint;

import android.content.res.Resources;
import android.os.Bundle;
import com.android.settings.SetupWizardUtils;

public class SetupFODScreenProtectorWarningActivity extends FODScreenProtectorWarningActivity {
    public int getMetricsCategory() {
        return 49;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        findViewById(16908290).setSystemUiVisibility(4194304);
    }

    /* access modifiers changed from: protected */
    public void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
        super.onApplyThemeResource(theme, SetupWizardUtils.getTheme(this, getIntent()), z);
    }
}

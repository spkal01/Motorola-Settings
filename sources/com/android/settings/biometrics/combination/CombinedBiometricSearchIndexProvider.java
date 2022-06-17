package com.android.settings.biometrics.combination;

import android.content.Context;
import android.content.pm.PackageManager;
import com.android.settings.search.BaseSearchIndexProvider;

public class CombinedBiometricSearchIndexProvider extends BaseSearchIndexProvider {
    public CombinedBiometricSearchIndexProvider(int i) {
        super(i);
    }

    /* access modifiers changed from: protected */
    public boolean isPageSearchEnabled(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature("android.hardware.biometrics.face") && packageManager.hasSystemFeature("android.hardware.fingerprint");
    }
}

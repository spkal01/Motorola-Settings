package com.android.settings.biometrics.fingerprint;

import android.os.Bundle;
import androidx.fragment.app.FragmentResultListener;
import com.android.settings.biometrics.fingerprint.FingerprintSettings;

/* renamed from: com.android.settings.biometrics.fingerprint.FingerprintSettings$FingerprintSettingsFragment$$ExternalSyntheticLambda2 */
public final /* synthetic */ class C0787xd2803eab implements FragmentResultListener {
    public final /* synthetic */ FingerprintSettings.FingerprintSettingsFragment f$0;

    public /* synthetic */ C0787xd2803eab(FingerprintSettings.FingerprintSettingsFragment fingerprintSettingsFragment) {
        this.f$0 = fingerprintSettingsFragment;
    }

    public final void onFragmentResult(String str, Bundle bundle) {
        this.f$0.lambda$onCreate$1(str, bundle);
    }
}

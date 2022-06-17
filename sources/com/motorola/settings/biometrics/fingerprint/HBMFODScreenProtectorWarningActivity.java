package com.motorola.settings.biometrics.fingerprint;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.android.settings.Utils;
import com.android.settings.biometrics.fingerprint.FingerprintAuthenticateSidecar;

public class HBMFODScreenProtectorWarningActivity extends FODScreenProtectorWarningActivity {
    FingerprintAuthenticateSidecar.Listener mAuthenticateListener = new FingerprintAuthenticateSidecar.Listener() {
        public void onAuthenticationError(int i, CharSequence charSequence) {
        }

        public void onAuthenticationFailed() {
        }

        public void onAuthenticationHelp(int i, CharSequence charSequence) {
        }

        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult) {
        }
    };
    private FingerprintAuthenticateSidecar mAuthenticateSidecar;
    private FingerprintManager mFingerprintManager;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mFingerprintManager = Utils.getFingerprintManagerOrNull(this);
        FingerprintAuthenticateSidecar fingerprintAuthenticateSidecar = (FingerprintAuthenticateSidecar) getSupportFragmentManager().findFragmentByTag("authenticate_sidecar");
        this.mAuthenticateSidecar = fingerprintAuthenticateSidecar;
        if (fingerprintAuthenticateSidecar == null) {
            this.mAuthenticateSidecar = new FingerprintAuthenticateSidecar();
            getSupportFragmentManager().beginTransaction().add((Fragment) this.mAuthenticateSidecar, "authenticate_sidecar").commit();
        }
        this.mAuthenticateSidecar.setFingerprintManager(this.mFingerprintManager);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.mAuthenticateSidecar.startAuthentication(this.mUserId);
        this.mAuthenticateSidecar.setListener(this.mAuthenticateListener);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        stopAuthentication();
        super.onPause();
    }

    private void stopAuthentication() {
        FingerprintAuthenticateSidecar fingerprintAuthenticateSidecar = this.mAuthenticateSidecar;
        if (fingerprintAuthenticateSidecar != null) {
            fingerprintAuthenticateSidecar.setListener((FingerprintAuthenticateSidecar.Listener) null);
            this.mAuthenticateSidecar.stopAuthentication();
        }
    }
}

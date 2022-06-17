package com.android.settings.biometrics.fingerprint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.android.settings.C1992R$string;

public class FingerprintEnrollParentalConsent extends FingerprintEnrollIntroduction {
    public int getMetricsCategory() {
        return 1892;
    }

    /* access modifiers changed from: protected */
    public boolean onSetOrConfirmCredentials(Intent intent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setDescriptionText(C1992R$string.f73xb68b81de);
    }

    /* access modifiers changed from: protected */
    public void onNextButtonClick(View view) {
        onConsentResult(true);
    }

    /* access modifiers changed from: protected */
    public void onSkipButtonClick(View view) {
        onConsentResult(false);
    }

    /* access modifiers changed from: protected */
    public void onEnrollmentSkipped(Intent intent) {
        onConsentResult(false);
    }

    /* access modifiers changed from: protected */
    public void onFinishedEnrolling(Intent intent) {
        onConsentResult(true);
    }

    private void onConsentResult(boolean z) {
        Intent intent = new Intent();
        intent.putExtra("sensor_modality", 2);
        setResult(z ? 4 : 5, intent);
        finish();
    }

    /* access modifiers changed from: protected */
    public int getFooterTitle1() {
        return C1992R$string.f74x4f718905;
    }

    /* access modifiers changed from: protected */
    public int getFooterMessage2() {
        return C1992R$string.f82x9550dc28;
    }

    /* access modifiers changed from: protected */
    public int getFooterMessage3() {
        return C1992R$string.f83x9550dc29;
    }

    /* access modifiers changed from: protected */
    public int getFooterMessage4() {
        return C1992R$string.f84x9550dc2a;
    }

    /* access modifiers changed from: protected */
    public int getFooterMessage5() {
        return C1992R$string.f85x9550dc2b;
    }

    /* access modifiers changed from: protected */
    public int getHeaderResDefault() {
        return C1992R$string.security_settings_fingerprint_enroll_consent_introduction_title;
    }
}

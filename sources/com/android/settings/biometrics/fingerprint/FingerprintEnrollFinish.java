package com.android.settings.biometrics.fingerprint;

import android.content.ComponentName;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.android.settings.C1977R$anim;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import com.android.settings.Utils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.motorola.settings.biometrics.fingerprint.FingerprintPowerTouch;
import com.motorola.settings.biometrics.fingerprint.FingerprintUtils;

public class FingerprintEnrollFinish extends FingerprintEnrollBase {
    static final String FINGERPRINT_SUGGESTION_ACTIVITY = "com.android.settings.SetupFingerprintSuggestionActivity";
    static final int REQUEST_ADD_ANOTHER = 1;

    public int getMetricsCategory() {
        return 242;
    }

    /* access modifiers changed from: protected */
    public boolean trackAbandonAction() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1987R$layout.fingerprint_enroll_finish);
        setHeaderText(C1992R$string.security_settings_fingerprint_enroll_finish_title);
        setDescriptionText(C1992R$string.security_settings_fingerprint_enroll_finish_message);
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        footerBarMixin.setSecondaryButton(new FooterButton.Builder(this).setText(C1992R$string.security_settings_fingerprint_enroll_done).setListener(new FingerprintEnrollFinish$$ExternalSyntheticLambda0(this)).setButtonType(5).setTheme(C1993R$style.FingerprintButton_Secondary).build());
        this.mFooterBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(C1992R$string.fingerprint_enroll_button_add).setButtonType(7).setTheme(C1993R$style.FingerprintButton_Primary).build());
    }

    public void onBackPressed() {
        super.onBackPressed();
        updateFingerprintSuggestionEnableState();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        FooterButton primaryButton = this.mFooterBarMixin.getPrimaryButton();
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        boolean z = false;
        if (fingerprintManagerOrNull != null && fingerprintManagerOrNull.getEnrolledFingerprints(this.mUserId).size() >= getResources().getInteger(17694823)) {
            z = true;
        }
        if (z) {
            primaryButton.setVisibility(4);
        } else {
            primaryButton.setOnClickListener(new FingerprintEnrollFinish$$ExternalSyntheticLambda1(this));
        }
    }

    /* access modifiers changed from: protected */
    public void onNextButtonClick(View view) {
        updateFingerprintSuggestionEnableState();
        setResult(1);
        if (WizardManagerHelper.isAnySetupWizard(getIntent())) {
            postEnroll();
        } else if (this.mFromSettingsSummary) {
            if (FingerprintUtils.shouldShowSideFPSFeatures(getBaseContext())) {
                explorePowerTouch();
            } else {
                launchFingerprintSettings();
            }
        } else if (getIntent().getBooleanExtra("from_fingerprint_settings", false) && FingerprintUtils.shouldShowSideFPSFeatures(getBaseContext())) {
            explorePowerTouch();
        }
        finish();
    }

    private void explorePowerTouch() {
        Intent intentForClassName = getIntentForClassName(FingerprintPowerTouch.class.getName());
        intentForClassName.setFlags(603979776);
        startActivity(intentForClassName);
    }

    private void updateFingerprintSuggestionEnableState() {
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        if (fingerprintManagerOrNull != null) {
            int size = fingerprintManagerOrNull.getEnrolledFingerprints(this.mUserId).size();
            boolean z = true;
            getPackageManager().setComponentEnabledSetting(new ComponentName(getApplicationContext(), FINGERPRINT_SUGGESTION_ACTIVITY), size == 1 ? 1 : 2, 1);
            StringBuilder sb = new StringBuilder();
            sb.append("com.android.settings.SetupFingerprintSuggestionActivity enabled state = ");
            if (size != 1) {
                z = false;
            }
            sb.append(z);
            Log.d("FingerprintEnrollFinish", sb.toString());
        }
    }

    private void postEnroll() {
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        if (fingerprintManagerOrNull != null) {
            fingerprintManagerOrNull.revokeChallenge(this.mUserId, this.mChallenge);
        }
    }

    private void launchFingerprintSettings() {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", FingerprintSettings.class.getName());
        intent.putExtra("hw_auth_token", this.mToken);
        intent.setFlags(603979776);
        intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
        intent.putExtra("challenge", this.mChallenge);
        startActivity(intent);
        overridePendingTransition(C1977R$anim.sud_slide_back_in, C1977R$anim.sud_slide_back_out);
    }

    /* access modifiers changed from: private */
    public void onAddAnotherButtonClick(View view) {
        startActivityForResult(getFingerprintEnrollingIntent(), 1);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        updateFingerprintSuggestionEnableState();
        if (i != 1 || i2 == 0) {
            super.onActivityResult(i, i2, intent);
            return;
        }
        setResult(i2, intent);
        finish();
    }
}

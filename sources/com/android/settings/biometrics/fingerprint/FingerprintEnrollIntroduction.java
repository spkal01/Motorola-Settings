package com.android.settings.biometrics.fingerprint;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollIntroduction;
import com.android.settings.biometrics.BiometricUtils;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.span.LinkSpan;
import java.util.Objects;

public class FingerprintEnrollIntroduction extends BiometricEnrollIntroduction {
    private FingerprintManager mFingerprintManager;
    private FooterButton mPrimaryFooterButton;
    private FooterButton mSecondaryFooterButton;

    /* access modifiers changed from: protected */
    public String getExtraKeyForBiometric() {
        return "for_fingerprint";
    }

    public int getMetricsCategory() {
        return 243;
    }

    public int getModality() {
        return 2;
    }

    /* access modifiers changed from: protected */
    public boolean isEnrollingFingerprint() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        getIntent().putExtra(WizardManagerHelper.EXTRA_ALLOW_CUSTOMIZATION_IN_SETUP_FLOW, true);
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        this.mFingerprintManager = fingerprintManagerOrNull;
        if (fingerprintManagerOrNull == null) {
            Log.e("FingerprintIntro", "Null FingerprintManager");
            finish();
            return;
        }
        super.onCreate(bundle);
        ((ImageView) findViewById(C1985R$id.icon_fingerprint)).getDrawable().setColorFilter(getIconColorFilter());
        ((ImageView) findViewById(C1985R$id.icon_device_locked)).getDrawable().setColorFilter(getIconColorFilter());
        ((ImageView) findViewById(C1985R$id.icon_trash_can)).getDrawable().setColorFilter(getIconColorFilter());
        ((ImageView) findViewById(C1985R$id.icon_info)).getDrawable().setColorFilter(getIconColorFilter());
        ((ImageView) findViewById(C1985R$id.icon_link)).getDrawable().setColorFilter(getIconColorFilter());
        ((TextView) findViewById(C1985R$id.footer_message_2)).setText(getFooterMessage2());
        ((TextView) findViewById(C1985R$id.footer_message_3)).setText(getFooterMessage3());
        ((TextView) findViewById(C1985R$id.footer_message_4)).setText(getFooterMessage4());
        ((TextView) findViewById(C1985R$id.footer_message_5)).setText(getFooterMessage5());
        ((TextView) findViewById(C1985R$id.footer_title_1)).setText(getFooterTitle1());
        ((TextView) findViewById(C1985R$id.footer_title_2)).setText(getFooterTitle2());
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        boolean z = false;
        boolean z2 = i == 2 || i == 6;
        if (i2 == 2 || i2 == 11 || i2 == 1) {
            z = true;
        }
        if (z2 && z) {
            intent = setSkipPendingEnroll(intent);
        }
        super.onActivityResult(i, i2, intent);
    }

    /* access modifiers changed from: protected */
    public void onCancelButtonClick(View view) {
        setResult(2, setSkipPendingEnroll(new Intent()));
        finish();
    }

    /* access modifiers changed from: protected */
    public void onSkipButtonClick(View view) {
        onCancelButtonClick(view);
    }

    /* access modifiers changed from: package-private */
    public int getNegativeButtonTextId() {
        return C1992R$string.security_fingerprint_cancel;
    }

    /* access modifiers changed from: protected */
    public int getFooterTitle1() {
        return C1992R$string.security_settings_fingerprint_enroll_introduction_footer_title_1;
    }

    /* access modifiers changed from: protected */
    public int getFooterTitle2() {
        return C1992R$string.security_settings_fingerprint_enroll_introduction_footer_title_2;
    }

    /* access modifiers changed from: protected */
    public int getFooterMessage2() {
        return C1992R$string.f78xdb87ba0d;
    }

    /* access modifiers changed from: protected */
    public int getFooterMessage3() {
        return C1992R$string.f79xdb87ba0e;
    }

    /* access modifiers changed from: protected */
    public int getFooterMessage4() {
        return C1992R$string.f80xdb87ba0f;
    }

    /* access modifiers changed from: protected */
    public int getFooterMessage5() {
        return C1992R$string.f81xdb87ba10;
    }

    /* access modifiers changed from: protected */
    public boolean isDisabledByAdmin() {
        return RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(this, 32, this.mUserId) != null;
    }

    /* access modifiers changed from: protected */
    public int getLayoutResource() {
        return C1987R$layout.fingerprint_enroll_introduction;
    }

    /* access modifiers changed from: protected */
    public int getHeaderResDisabledByAdmin() {
        return C1992R$string.f76x2980692c;
    }

    /* access modifiers changed from: protected */
    public int getHeaderResDefault() {
        return C1992R$string.security_settings_fingerprint_enroll_introduction_title;
    }

    /* access modifiers changed from: protected */
    public int getDescriptionResDisabledByAdmin() {
        return C1992R$string.f75x4ab1f9db;
    }

    /* access modifiers changed from: protected */
    public FooterButton getCancelButton() {
        FooterBarMixin footerBarMixin = this.mFooterBarMixin;
        if (footerBarMixin != null) {
            return footerBarMixin.getSecondaryButton();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public FooterButton getNextButton() {
        FooterBarMixin footerBarMixin = this.mFooterBarMixin;
        if (footerBarMixin != null) {
            return footerBarMixin.getPrimaryButton();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public TextView getErrorTextView() {
        return (TextView) findViewById(C1985R$id.error_text);
    }

    /* access modifiers changed from: protected */
    public int checkMaxEnrolled() {
        FingerprintManager fingerprintManager = this.mFingerprintManager;
        if (fingerprintManager == null) {
            return C1992R$string.fingerprint_intro_error_unknown;
        }
        if (this.mFingerprintManager.getEnrolledFingerprints(this.mUserId).size() >= ((FingerprintSensorPropertiesInternal) fingerprintManager.getSensorPropertiesInternal().get(0)).maxEnrollmentsPerUser) {
            return C1992R$string.fingerprint_intro_error_max;
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public void getChallenge(BiometricEnrollIntroduction.GenerateChallengeCallback generateChallengeCallback) {
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        this.mFingerprintManager = fingerprintManagerOrNull;
        if (fingerprintManagerOrNull == null) {
            generateChallengeCallback.onChallengeGenerated(0, 0, 0);
            return;
        }
        int i = this.mUserId;
        Objects.requireNonNull(generateChallengeCallback);
        fingerprintManagerOrNull.generateChallenge(i, new FingerprintEnrollIntroduction$$ExternalSyntheticLambda0(generateChallengeCallback));
    }

    /* access modifiers changed from: protected */
    public Intent getEnrollingIntent() {
        Intent intent = new Intent(this, FingerprintEnrollFindSensor.class);
        if (BiometricUtils.containsGatekeeperPasswordHandle(getIntent())) {
            intent.putExtra("gk_pw_handle", BiometricUtils.getGatekeeperPasswordHandle(getIntent()));
        }
        return intent;
    }

    /* access modifiers changed from: protected */
    public int getConfirmLockTitleResId() {
        return C1992R$string.security_settings_fingerprint_preference_title;
    }

    public void onClick(LinkSpan linkSpan) {
        if ("url".equals(linkSpan.getId())) {
            Intent helpIntent = HelpUtils.getHelpIntent(this, getString(C1992R$string.help_url_fingerprint), getClass().getName());
            if (helpIntent == null) {
                Log.w("FingerprintIntro", "Null help intent.");
                return;
            }
            try {
                startActivityForResult(helpIntent, 3);
            } catch (ActivityNotFoundException e) {
                Log.w("FingerprintIntro", "Activity was not found for intent, " + e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public FooterButton getPrimaryFooterButton() {
        if (this.mPrimaryFooterButton == null) {
            this.mPrimaryFooterButton = new FooterButton.Builder(this).setText(C1992R$string.security_fingerprint_setup).setListener(new FingerprintEnrollIntroduction$$ExternalSyntheticLambda2(this)).setButtonType(6).setTheme(C1993R$style.FingerprintIntroButton_Primary).build();
        }
        return this.mPrimaryFooterButton;
    }

    /* access modifiers changed from: protected */
    public FooterButton getSecondaryFooterButton() {
        if (this.mSecondaryFooterButton == null) {
            this.mSecondaryFooterButton = new FooterButton.Builder(this).setText(getNegativeButtonTextId()).setListener(new FingerprintEnrollIntroduction$$ExternalSyntheticLambda1(this)).setButtonType(7).setTheme(C1993R$style.FingerprintIntroButton_Secondary).build();
        }
        return this.mSecondaryFooterButton;
    }

    /* access modifiers changed from: protected */
    public int getAgreeButtonTextRes() {
        return C1992R$string.security_fingerprint_setup;
    }

    /* access modifiers changed from: protected */
    public int getMoreButtonTextRes() {
        return C1992R$string.security_settings_face_enroll_introduction_more;
    }

    protected static Intent setSkipPendingEnroll(Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.putExtra("skip_pending_enroll", true);
        return intent;
    }
}

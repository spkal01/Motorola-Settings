package com.android.settings.biometrics.face;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.face.FaceManager;
import android.hardware.face.FaceSensorPropertiesInternal;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.C1980R$bool;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollIntroduction;
import com.android.settings.biometrics.BiometricUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.span.LinkSpan;
import com.motorola.settings.biometrics.face.FaceUtils;
import com.motorola.settings.biometrics.face.SetupMotoFaceEnrollSuggestion;
import java.util.Objects;

public class FaceEnrollIntroduction extends BiometricEnrollIntroduction {
    private FaceFeatureProvider mFaceFeatureProvider;
    private FaceManager mFaceManager;
    private boolean mForRedo;
    private FooterButton mPrimaryFooterButton;
    private FooterButton mSecondaryFooterButton;

    /* access modifiers changed from: protected */
    public boolean generateChallengeOnCreate() {
        return true;
    }

    /* access modifiers changed from: protected */
    public String getExtraKeyForBiometric() {
        return "for_face";
    }

    public int getMetricsCategory() {
        return 1506;
    }

    public int getModality() {
        return 8;
    }

    public void onClick(LinkSpan linkSpan) {
    }

    /* access modifiers changed from: protected */
    public void onSkipButtonClick(View view) {
        if (!BiometricUtils.tryStartingNextBiometricEnroll(this, 6)) {
            super.onSkipButtonClick(view);
        }
    }

    /* access modifiers changed from: protected */
    public void onEnrollmentSkipped(Intent intent) {
        if (!BiometricUtils.tryStartingNextBiometricEnroll(this, 6)) {
            super.onEnrollmentSkipped(intent);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishedEnrolling(Intent intent) {
        if (!BiometricUtils.tryStartingNextBiometricEnroll(this, 6)) {
            super.onFinishedEnrolling(intent);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ((ImageView) findViewById(C1985R$id.icon_glasses)).getBackground().setColorFilter(getIconColorFilter());
        ((ImageView) findViewById(C1985R$id.icon_looking)).getBackground().setColorFilter(getIconColorFilter());
        ((TextView) findViewById(C1985R$id.info_message_glasses)).setText(getInfoMessageGlasses());
        ((TextView) findViewById(C1985R$id.info_message_looking)).setText(getInfoMessageLooking());
        ((TextView) findViewById(C1985R$id.title_in_control)).setText(getInControlTitle());
        ((TextView) findViewById(C1985R$id.how_message)).setText(getHowMessage());
        ((TextView) findViewById(C1985R$id.message_in_control)).setText(getInControlMessage());
        if (getResources().getBoolean(C1980R$bool.config_face_intro_show_require_eyes)) {
            ((LinearLayout) findViewById(C1985R$id.info_row_require_eyes)).setVisibility(0);
            ((ImageView) findViewById(C1985R$id.icon_require_eyes)).getBackground().setColorFilter(getIconColorFilter());
            ((TextView) findViewById(C1985R$id.info_message_require_eyes)).setText(getInfoMessageRequireEyes());
        }
        this.mForRedo = getIntent().getBooleanExtra("for_redo", false);
        this.mFaceManager = Utils.getFaceManagerOrNull(this);
        this.mFaceFeatureProvider = FeatureFactory.getFactory(getApplicationContext()).getFaceFeatureProvider();
        if (this.mToken == null && BiometricUtils.containsGatekeeperPasswordHandle(getIntent()) && generateChallengeOnCreate()) {
            this.mFooterBarMixin.getPrimaryButton().setEnabled(false);
            this.mFaceManager.generateChallenge(this.mUserId, new FaceEnrollIntroduction$$ExternalSyntheticLambda1(this));
        }
        if (FaceUtils.isMotoFaceUnlock() && this.mHasPassword && this.mToken != null) {
            openMotoFaceUnlock();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(int i, int i2, long j) {
        this.mToken = BiometricUtils.requestGatekeeperHat((Context) this, getIntent(), this.mUserId, j);
        this.mSensorId = i;
        this.mChallenge = j;
        this.mFooterBarMixin.getPrimaryButton().setEnabled(true);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (!FaceUtils.isMotoFaceUnlock()) {
            return;
        }
        if (i != 1) {
            if (i != 4) {
                if (i == 5) {
                    if (i2 == 1 || i2 == -1) {
                        FaceManager faceManager = this.mFaceManager;
                        if (faceManager != null && faceManager.getEnrolledFaces(this.mUserId).size() > 0) {
                            SetupMotoFaceEnrollSuggestion.disableComponent(this);
                        }
                        setResult(1);
                        finish();
                        return;
                    }
                    setResult(0);
                    finish();
                }
            } else if (i2 == -1 && intent != null) {
                checkTokenAndOpenMotoFaceUnlock(intent);
            }
        } else if (i2 == 1) {
            checkTokenAndOpenMotoFaceUnlock(intent);
        }
    }

    /* access modifiers changed from: protected */
    public int getInfoMessageGlasses() {
        return C1992R$string.security_settings_face_enroll_introduction_info_glasses;
    }

    /* access modifiers changed from: protected */
    public int getInfoMessageLooking() {
        return C1992R$string.security_settings_face_enroll_introduction_info_looking;
    }

    /* access modifiers changed from: protected */
    public int getInfoMessageRequireEyes() {
        return C1992R$string.security_settings_face_enroll_introduction_info_gaze;
    }

    /* access modifiers changed from: protected */
    public int getHowMessage() {
        return C1992R$string.security_settings_face_enroll_introduction_how_message;
    }

    /* access modifiers changed from: protected */
    public int getInControlTitle() {
        return C1992R$string.security_settings_face_enroll_introduction_control_title;
    }

    /* access modifiers changed from: protected */
    public int getInControlMessage() {
        return C1992R$string.security_settings_face_enroll_introduction_control_message;
    }

    /* access modifiers changed from: protected */
    public boolean isDisabledByAdmin() {
        return RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(this, 128, this.mUserId) != null;
    }

    /* access modifiers changed from: protected */
    public int getLayoutResource() {
        if (FaceUtils.isMotoFaceUnlock()) {
            return C1987R$layout.face_enroll_introduction_moto;
        }
        return C1987R$layout.face_enroll_introduction;
    }

    /* access modifiers changed from: protected */
    public int getHeaderResDisabledByAdmin() {
        return C1992R$string.security_settings_face_enroll_introduction_title_unlock_disabled;
    }

    /* access modifiers changed from: protected */
    public int getHeaderResDefault() {
        return C1992R$string.security_settings_face_enroll_introduction_title;
    }

    /* access modifiers changed from: protected */
    public int getDescriptionResDisabledByAdmin() {
        return C1992R$string.f69x7c35c12e;
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

    private boolean maxFacesEnrolled() {
        FaceManager faceManager = this.mFaceManager;
        if (faceManager == null || this.mFaceManager.getEnrolledFaces(this.mUserId).size() < ((FaceSensorPropertiesInternal) faceManager.getSensorPropertiesInternal().get(0)).maxEnrollmentsPerUser) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public int checkMaxEnrolled() {
        if (this.mFaceManager == null) {
            return C1992R$string.face_intro_error_unknown;
        }
        if (maxFacesEnrolled()) {
            return C1992R$string.face_intro_error_max;
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public void getChallenge(BiometricEnrollIntroduction.GenerateChallengeCallback generateChallengeCallback) {
        FaceManager faceManagerOrNull = Utils.getFaceManagerOrNull(this);
        this.mFaceManager = faceManagerOrNull;
        if (faceManagerOrNull == null) {
            generateChallengeCallback.onChallengeGenerated(0, 0, 0);
            return;
        }
        int i = this.mUserId;
        Objects.requireNonNull(generateChallengeCallback);
        faceManagerOrNull.generateChallenge(i, new FaceEnrollIntroduction$$ExternalSyntheticLambda0(generateChallengeCallback));
    }

    /* access modifiers changed from: protected */
    public Intent getEnrollingIntent() {
        Intent intent = new Intent(this, FaceEnrollEducation.class);
        WizardManagerHelper.copyWizardManagerExtras(getIntent(), intent);
        return intent;
    }

    /* access modifiers changed from: protected */
    public int getConfirmLockTitleResId() {
        return C1992R$string.security_settings_face_preference_title;
    }

    /* access modifiers changed from: protected */
    public FooterButton getPrimaryFooterButton() {
        if (this.mPrimaryFooterButton == null) {
            this.mPrimaryFooterButton = new FooterButton.Builder(this).setText(C1992R$string.security_settings_face_enroll_introduction_agree).setButtonType(6).setListener(new FaceEnrollIntroduction$$ExternalSyntheticLambda4(this)).setTheme(C1993R$style.SudGlifButton_Primary).build();
        }
        return this.mPrimaryFooterButton;
    }

    /* access modifiers changed from: protected */
    public FooterButton getSecondaryFooterButton() {
        if (this.mSecondaryFooterButton == null) {
            this.mSecondaryFooterButton = new FooterButton.Builder(this).setText(C1992R$string.security_settings_face_enroll_introduction_no_thanks).setListener(new FaceEnrollIntroduction$$ExternalSyntheticLambda3(this)).setButtonType(5).setTheme(C1993R$style.SudGlifButton_Primary).build();
        }
        return this.mSecondaryFooterButton;
    }

    /* access modifiers changed from: protected */
    public int getAgreeButtonTextRes() {
        return C1992R$string.security_settings_fingerprint_enroll_introduction_agree;
    }

    /* access modifiers changed from: protected */
    public int getMoreButtonTextRes() {
        return C1992R$string.security_settings_face_enroll_introduction_more;
    }

    private void openMotoFaceUnlock() {
        ComponentName componentName;
        Intent intent = new Intent();
        intent.putExtra("hw_auth_token", this.mToken);
        int i = this.mUserId;
        if (i != -10000) {
            intent.putExtra("android.intent.extra.USER_ID", i);
        }
        if (this.mForRedo) {
            componentName = new ComponentName("com.motorola.faceunlock", "com.motorola.faceunlock.FaceEnrollActivity");
        } else {
            componentName = new ComponentName("com.motorola.faceunlock", "com.motorola.faceunlock.SetupFaceIntroActivity");
        }
        intent.setComponent(componentName);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 5);
        }
    }

    private void checkTokenAndOpenMotoFaceUnlock(Intent intent) {
        if (this.mToken == null) {
            this.mFaceManager.generateChallenge(this.mUserId, new FaceEnrollIntroduction$$ExternalSyntheticLambda2(this, intent));
        } else {
            openMotoFaceUnlock();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkTokenAndOpenMotoFaceUnlock$1(Intent intent, int i, int i2, long j) {
        if (this.mToken == null) {
            this.mToken = BiometricUtils.requestGatekeeperHat((Context) this, intent, this.mUserId, j);
            this.mSensorId = i;
            this.mChallenge = j;
            BiometricUtils.removeGatekeeperPasswordHandle((Context) this, intent);
            openMotoFaceUnlock();
        }
    }
}

package com.android.settings.biometrics.fingerprint;

import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.C1977R$anim;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1991R$raw;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollSidecar;
import com.android.settings.biometrics.BiometricUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.motorola.settings.biometrics.fingerprint.FODScreenProtectorWarningActivity;
import com.motorola.settings.biometrics.fingerprint.FingerprintUtils;
import java.util.List;

public class FingerprintEnrollFindSensor extends FingerprintEnrollBase implements BiometricEnrollSidecar.Listener {
    private FingerprintFindSensorAnimation mAnimation;
    private ImageView mAnimationView;
    private boolean mCanAssumeUdfps;
    private final Runnable mDelayedAdvanceRunnable = new Runnable() {
        public void run() {
            FingerprintEnrollFindSensor.this.proceedToEnrolling(false);
        }
    };
    private boolean mFoundSensor;
    private boolean mLaunchedScreenProtectorWarning;
    private boolean mNextClicked;
    private FingerprintEnrollSidecar mSidecar;

    public int getMetricsCategory() {
        return 241;
    }

    public void onEnrollmentHelp(int i, CharSequence charSequence) {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        List sensorPropertiesInternal = ((FingerprintManager) getSystemService(FingerprintManager.class)).getSensorPropertiesInternal();
        boolean z = true;
        if (sensorPropertiesInternal == null || sensorPropertiesInternal.size() != 1 || !((FingerprintSensorPropertiesInternal) sensorPropertiesInternal.get(0)).isAnyUdfpsType() || FingerprintUtils.isMotoFODEnabled(this)) {
            z = false;
        }
        this.mCanAssumeUdfps = z;
        setContentView(getContentView());
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        FooterButton.Builder builder = new FooterButton.Builder(this);
        if (!(this instanceof SetupFingerprintEnrollFindSensor)) {
            i = C1992R$string.cancel;
        } else {
            i = C1992R$string.security_settings_fingerprint_enroll_enrolling_skip;
        }
        footerBarMixin.setSecondaryButton(builder.setText(i).setListener(new FingerprintEnrollFindSensor$$ExternalSyntheticLambda1(this)).setButtonType(7).setTheme(C1993R$style.FingerprintButton_Secondary).build());
        if (this.mCanAssumeUdfps) {
            setHeaderText(C1992R$string.security_settings_udfps_enroll_find_sensor_title);
            setDescriptionText(C1992R$string.security_settings_udfps_enroll_find_sensor_message);
            this.mFooterBarMixin.setPrimaryButton(new FooterButton.Builder(this).setText(C1992R$string.security_settings_udfps_enroll_find_sensor_start_button).setListener(new FingerprintEnrollFindSensor$$ExternalSyntheticLambda2(this)).setButtonType(5).setTheme(C1993R$style.SudGlifButton_Primary).build());
            LottieAnimationView lottieAnimationView = (LottieAnimationView) findViewById(C1985R$id.illustration_lottie);
            if (((AccessibilityManager) getSystemService(AccessibilityManager.class)).isEnabled()) {
                lottieAnimationView.setAnimation(C1991R$raw.udfps_edu_a11y_lottie);
            }
        } else {
            setHeaderText(C1992R$string.security_settings_fingerprint_enroll_find_sensor_title);
            setDescriptionText(C1992R$string.security_settings_fingerprint_enroll_find_sensor_message);
        }
        if (bundle != null) {
            this.mLaunchedScreenProtectorWarning = bundle.getBoolean("launched_screen_protect_warning");
        }
        if (this.mToken == null && BiometricUtils.containsGatekeeperPasswordHandle(getIntent())) {
            ((FingerprintManager) getSystemService(FingerprintManager.class)).generateChallenge(this.mUserId, new FingerprintEnrollFindSensor$$ExternalSyntheticLambda0(this));
        } else if (this.mToken == null) {
            throw new IllegalStateException("HAT and GkPwHandle both missing...");
        } else if (!FingerprintUtils.isMotoFODEnabled(this) || this.mLaunchedScreenProtectorWarning) {
            startLookingForFingerprint();
        } else {
            launchScreenProtectorWarning();
        }
        this.mAnimation = null;
        if (!this.mCanAssumeUdfps) {
            ImageView imageView = (ImageView) findViewById(C1985R$id.fingerprint_sensor_location_animation);
            if (imageView instanceof FingerprintFindSensorAnimation) {
                this.mAnimation = (FingerprintFindSensorAnimation) imageView;
            } else if (imageView instanceof LottieAnimationView) {
                ((LottieAnimationView) imageView).setAnimation(C1991R$raw.fingerprint_location_animation);
            }
            this.mAnimationView = imageView;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(int i, int i2, long j) {
        this.mChallenge = j;
        this.mSensorId = i;
        this.mToken = BiometricUtils.requestGatekeeperHat((Context) this, getIntent(), this.mUserId, j);
        getIntent().putExtra("hw_auth_token", this.mToken);
        if (!FingerprintUtils.isMotoFODEnabled(this) || this.mLaunchedScreenProtectorWarning) {
            startLookingForFingerprint();
        } else {
            launchScreenProtectorWarning();
        }
    }

    public void onBackPressed() {
        stopLookingForFingerprint();
        super.onBackPressed();
    }

    /* access modifiers changed from: protected */
    public int getContentView() {
        if (this.mCanAssumeUdfps) {
            return C1987R$layout.udfps_enroll_find_sensor_layout;
        }
        return C1987R$layout.fingerprint_enroll_find_sensor;
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        FingerprintFindSensorAnimation fingerprintFindSensorAnimation = this.mAnimation;
        if (fingerprintFindSensorAnimation != null) {
            fingerprintFindSensorAnimation.startAnimation();
        }
    }

    private void stopLookingForFingerprint() {
        FingerprintEnrollSidecar fingerprintEnrollSidecar = this.mSidecar;
        if (fingerprintEnrollSidecar != null) {
            fingerprintEnrollSidecar.setListener((BiometricEnrollSidecar.Listener) null);
            this.mSidecar.cancelEnrollment();
            getSupportFragmentManager().beginTransaction().remove(this.mSidecar).commitAllowingStateLoss();
            this.mSidecar = null;
        }
    }

    private void startLookingForFingerprint() {
        if (!this.mCanAssumeUdfps) {
            FingerprintEnrollSidecar fingerprintEnrollSidecar = (FingerprintEnrollSidecar) getSupportFragmentManager().findFragmentByTag("sidecar");
            this.mSidecar = fingerprintEnrollSidecar;
            if (fingerprintEnrollSidecar == null) {
                FingerprintEnrollSidecar fingerprintEnrollSidecar2 = new FingerprintEnrollSidecar();
                this.mSidecar = fingerprintEnrollSidecar2;
                fingerprintEnrollSidecar2.setEnrollReason(1);
                getSupportFragmentManager().beginTransaction().add((Fragment) this.mSidecar, "sidecar").commitAllowingStateLoss();
            }
            this.mSidecar.setListener(this);
        }
    }

    public void onEnrollmentProgressChange(int i, int i2) {
        this.mNextClicked = true;
        this.mFoundSensor = true;
        proceedToEnrolling(true);
    }

    public void onEnrollmentError(int i, CharSequence charSequence) {
        if ((!this.mNextClicked && !this.mFoundSensor) || i != 5) {
            FingerprintErrorDialog.showErrorDialog(this, i);
        } else if (this.mFoundSensor) {
            FingerprintFindSensorAnimation fingerprintFindSensorAnimation = this.mAnimation;
            if (fingerprintFindSensorAnimation != null) {
                fingerprintFindSensorAnimation.pauseAnimation();
            }
            this.mAnimationView.setImageResource(C1983R$drawable.ic_fingerprint_found_sensor);
            this.mAnimationView.postDelayed(this.mDelayedAdvanceRunnable, 400);
        } else {
            proceedToEnrolling(false);
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        FingerprintFindSensorAnimation fingerprintFindSensorAnimation = this.mAnimation;
        if (fingerprintFindSensorAnimation != null) {
            fingerprintFindSensorAnimation.pauseAnimation();
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldFinishWhenBackgrounded() {
        return super.shouldFinishWhenBackgrounded() && !this.mNextClicked && !this.mLaunchedScreenProtectorWarning;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        FingerprintFindSensorAnimation fingerprintFindSensorAnimation = this.mAnimation;
        if (fingerprintFindSensorAnimation != null) {
            fingerprintFindSensorAnimation.stopAnimation();
        }
    }

    /* access modifiers changed from: private */
    public void onStartButtonClick(View view) {
        startActivityForResult(getFingerprintEnrollingIntent(), 5);
    }

    /* access modifiers changed from: protected */
    public void onSkipButtonClick(View view) {
        stopLookingForFingerprint();
        setResult(2);
        finish();
    }

    /* access modifiers changed from: private */
    public void proceedToEnrolling(boolean z) {
        FingerprintEnrollSidecar fingerprintEnrollSidecar = this.mSidecar;
        if (fingerprintEnrollSidecar != null) {
            if (!z || !fingerprintEnrollSidecar.cancelEnrollment()) {
                getSupportFragmentManager().beginTransaction().remove(this.mSidecar).commitAllowingStateLoss();
                this.mSidecar = null;
                this.mIsNotAbandon = true;
                startActivityForResult(getFingerprintEnrollingIntent(), 5);
            } else {
                return;
            }
        }
        this.mFoundSensor = false;
        this.mNextClicked = false;
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 4) {
            if (i2 != -1 || intent == null) {
                finish();
                return;
            }
            throw new IllegalStateException("Pretty sure this is dead code");
        } else if (i == 5) {
            if (i2 == 1 || i2 == 2 || i2 == 3) {
                setResult(i2);
                finish();
            } else if (Utils.getFingerprintManagerOrNull(this).getEnrolledFingerprints().size() >= getResources().getInteger(17694823)) {
                finish();
            } else {
                startLookingForFingerprint();
            }
        } else if (i != 3) {
            super.onActivityResult(i, i2, intent);
        } else if (i2 == 1) {
            overridePendingTransition(C1977R$anim.sud_slide_next_in, C1977R$anim.sud_slide_next_out);
            startLookingForFingerprint();
        } else {
            finish();
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("launched_screen_protect_warning", this.mLaunchedScreenProtectorWarning);
    }

    private void launchScreenProtectorWarning() {
        this.mLaunchedScreenProtectorWarning = true;
        startActivityForResult(getFODScreenProtectorWarningIntent(), 3);
    }

    /* access modifiers changed from: protected */
    public Intent getFODScreenProtectorWarningIntent() {
        return getIntentForClassName(FODScreenProtectorWarningActivity.class.getName());
    }
}

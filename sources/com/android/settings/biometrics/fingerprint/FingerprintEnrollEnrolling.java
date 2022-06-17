package com.android.settings.biometrics.fingerprint;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C1979R$attr;
import com.android.settings.C1981R$color;
import com.android.settings.C1982R$dimen;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.biometrics.BiometricEnrollSidecar;
import com.android.settings.biometrics.BiometricUtils;
import com.android.settings.biometrics.BiometricsEnrollEnrolling;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.motorola.settings.biometrics.fingerprint.FingerprintEnrollAlmostThere;
import com.motorola.settings.biometrics.fingerprint.FingerprintUtils;
import com.motorola.settings.biometrics.fingerprint.MotoLottieAnimView;
import com.motorola.settings.utils.MotoSurveyUtils;
import java.util.List;

public class FingerprintEnrollEnrolling extends BiometricsEnrollEnrolling {
    private static final AudioAttributes FINGERPRINT_ENROLLING_SONFICATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    private static final int[] FINGERPRINT_FILL_ANIM_STATES = {C1979R$attr.state_zero, C1979R$attr.state_one, C1979R$attr.state_two, C1979R$attr.state_three, C1979R$attr.state_four, C1979R$attr.state_five, C1979R$attr.state_six, C1979R$attr.state_seven, C1979R$attr.state_eight, C1979R$attr.state_nine, C1979R$attr.state_ten, C1979R$attr.state_eleven, C1979R$attr.state_twelve, C1979R$attr.state_thirteen, C1979R$attr.state_fourteen, C1979R$attr.state_fifteen, C1979R$attr.state_sixteen, C1979R$attr.state_seventeen, C1979R$attr.state_eighteen, C1979R$attr.state_nineteen, C1979R$attr.state_twenty};
    private static final VibrationEffect VIBRATE_EFFECT_ERROR = VibrationEffect.createWaveform(new long[]{0, 5, 55, 60}, -1);
    private AccessibilityManager mAccessibilityManager;
    /* access modifiers changed from: private */
    public boolean mAnimationCancelled;
    private boolean mCanAssumeUdfps;
    /* access modifiers changed from: private */
    public final Runnable mDelayedFinishRunnable = new Runnable() {
        public void run() {
            FingerprintEnrollEnrolling fingerprintEnrollEnrolling = FingerprintEnrollEnrolling.this;
            fingerprintEnrollEnrolling.launchFinish(fingerprintEnrollEnrolling.mToken);
        }
    };
    private TextView mErrorText;
    private Interpolator mFastOutLinearInInterpolator;
    private Interpolator mFastOutSlowInInterpolator;
    private AnimatedStateListDrawable mFillInAnimationDrawable;
    private int[] mFillInAnimationState = new int[0];
    private LevelListDrawable mFillInBackgroundDrawable;
    private final Animatable2.AnimationCallback mIconAnimationCallback = new Animatable2.AnimationCallback() {
        public void onAnimationEnd(Drawable drawable) {
            if (!FingerprintEnrollEnrolling.this.mAnimationCancelled) {
                FingerprintEnrollEnrolling.this.mProgressBar.post(new Runnable() {
                    public void run() {
                        FingerprintEnrollEnrolling.this.startIconAnimation();
                    }
                });
            }
        }
    };
    private AnimatedVectorDrawable mIconAnimationDrawable;
    private AnimatedVectorDrawable mIconBackgroundBlinksDrawable;
    private int mIconTouchCount;
    private boolean mIsAccessibilityEnabled;
    private boolean mIsMotoFODEnroll;
    private boolean mIsSetupWizard;
    private Interpolator mLinearOutSlowInInterpolator;
    private MotoLottieAnimView mMotoLottieAnimView;
    private OrientationEventListener mOrientationEventListener;
    /* access modifiers changed from: private */
    public int mPreviousRotation = 0;
    private int mProgress = 0;
    private ObjectAnimator mProgressAnim;
    private final Animator.AnimatorListener mProgressAnimationListener = new Animator.AnimatorListener() {
        public void onAnimationCancel(Animator animator) {
        }

        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
        }

        public void onAnimationEnd(Animator animator) {
            if (FingerprintEnrollEnrolling.this.mProgressBar.getProgress() >= 10000) {
                FingerprintEnrollEnrolling.this.mProgressBar.postDelayed(FingerprintEnrollEnrolling.this.mDelayedFinishRunnable, 250);
            }
        }
    };
    /* access modifiers changed from: private */
    public ProgressBar mProgressBar;
    private boolean mRestoring;
    private final Runnable mShowDialogRunnable = new Runnable() {
        public void run() {
            FingerprintEnrollEnrolling.this.showIconTouchDialog();
        }
    };
    private final Runnable mTouchAgainRunnable = new Runnable() {
        public void run() {
            FingerprintEnrollEnrolling fingerprintEnrollEnrolling = FingerprintEnrollEnrolling.this;
            fingerprintEnrollEnrolling.showError(fingerprintEnrollEnrolling.getString(C1992R$string.security_settings_fingerprint_enroll_lift_touch_again));
        }
    };
    private Vibrator mVibrator;

    public int getMetricsCategory() {
        return 240;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        ProgressBar progressBar;
        super.onCreate(bundle);
        this.mIsMotoFODEnroll = FingerprintUtils.isMotoFODEnabled(this);
        List sensorPropertiesInternal = ((FingerprintManager) getSystemService(FingerprintManager.class)).getSensorPropertiesInternal();
        boolean z = false;
        this.mCanAssumeUdfps = sensorPropertiesInternal.size() == 1 && ((FingerprintSensorPropertiesInternal) sensorPropertiesInternal.get(0)).isAnyUdfpsType() && !this.mIsMotoFODEnroll;
        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(AccessibilityManager.class);
        this.mAccessibilityManager = accessibilityManager;
        this.mIsAccessibilityEnabled = accessibilityManager.isEnabled();
        listenOrientationEvent();
        if (this.mCanAssumeUdfps) {
            if (BiometricUtils.isReverseLandscape(getApplicationContext())) {
                setContentView(C1987R$layout.udfps_enroll_enrolling_land);
            } else {
                setContentView(C1987R$layout.udfps_enroll_enrolling);
            }
            setDescriptionText(C1992R$string.security_settings_udfps_enroll_start_message);
        } else {
            if (this.mIsMotoFODEnroll) {
                setContentView(C1987R$layout.fod_fingerprint_enroll_enrolling_base);
                getLayout().getDescriptionTextView().setMinLines(4);
            } else {
                setContentView(C1987R$layout.fingerprint_enroll_enrolling);
            }
            setDescriptionText(C1992R$string.security_settings_fingerprint_enroll_start_message);
        }
        this.mIsSetupWizard = WizardManagerHelper.isAnySetupWizard(getIntent());
        if (this.mCanAssumeUdfps) {
            updateTitleAndDescription();
        } else {
            setHeaderText(C1992R$string.security_settings_fingerprint_enroll_start_title);
        }
        this.mErrorText = (TextView) findViewById(C1985R$id.error_text);
        this.mProgressBar = (ProgressBar) findViewById(C1985R$id.fingerprint_progress_bar);
        MotoLottieAnimView motoLottieAnimView = (MotoLottieAnimView) findViewById(C1985R$id.fingerprint_animation_view);
        this.mMotoLottieAnimView = motoLottieAnimView;
        if (this.mIsMotoFODEnroll && motoLottieAnimView == null && (progressBar = this.mProgressBar) != null) {
            progressBar.setBackgroundResource(C1983R$drawable.fod_fp_illustration);
        }
        this.mVibrator = (Vibrator) getSystemService(Vibrator.class);
        ProgressBar progressBar2 = this.mProgressBar;
        LayerDrawable layerDrawable = progressBar2 != null ? (LayerDrawable) progressBar2.getBackground() : null;
        if (layerDrawable != null) {
            Drawable findDrawableByLayerId = layerDrawable.findDrawableByLayerId(C1985R$id.fingerprint_animation);
            if (findDrawableByLayerId instanceof AnimatedVectorDrawable) {
                this.mIconAnimationDrawable = (AnimatedVectorDrawable) findDrawableByLayerId;
                this.mIconBackgroundBlinksDrawable = (AnimatedVectorDrawable) layerDrawable.findDrawableByLayerId(C1985R$id.fingerprint_background);
                this.mIconAnimationDrawable.registerAnimationCallback(this.mIconAnimationCallback);
                this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(this, 17563661);
                this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(this, 17563662);
                this.mFastOutLinearInInterpolator = AnimationUtils.loadInterpolator(this, 17563663);
            } else if (findDrawableByLayerId instanceof AnimatedStateListDrawable) {
                this.mFillInAnimationDrawable = (AnimatedStateListDrawable) findDrawableByLayerId;
                LevelListDrawable levelListDrawable = (LevelListDrawable) layerDrawable.findDrawableByLayerId(C1985R$id.fingerprint_background);
                this.mFillInBackgroundDrawable = levelListDrawable;
                levelListDrawable.setLevel(0);
                this.mProgressBar.setLayerType(1, (Paint) null);
            }
        }
        ProgressBar progressBar3 = this.mProgressBar;
        if (progressBar3 != null) {
            if (!this.mIsMotoFODEnroll) {
                progressBar3.setOnTouchListener(new FingerprintEnrollEnrolling$$ExternalSyntheticLambda0(this));
            }
            this.mProgressBar.getViewTreeObserver().addOnWindowFocusChangeListener(new FingerprintEnrollEnrolling$$ExternalSyntheticLambda1(this));
        }
        if (bundle != null) {
            z = true;
        }
        this.mRestoring = z;
        FingerprintUtils.checkPowerTouchEnabledInBackground(getApplicationContext());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreate$0(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            int i = this.mIconTouchCount + 1;
            this.mIconTouchCount = i;
            if (i == 3) {
                showIconTouchDialog();
            } else {
                this.mProgressBar.postDelayed(this.mShowDialogRunnable, 500);
            }
        } else if (motionEvent.getActionMasked() == 3 || motionEvent.getActionMasked() == 1) {
            this.mProgressBar.removeCallbacks(this.mShowDialogRunnable);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public BiometricEnrollSidecar getSidecar() {
        FingerprintEnrollSidecar fingerprintEnrollSidecar = new FingerprintEnrollSidecar();
        fingerprintEnrollSidecar.setEnrollReason(2);
        return fingerprintEnrollSidecar;
    }

    /* access modifiers changed from: protected */
    public boolean shouldStartAutomatically() {
        if (!isOkToEnroll()) {
            return false;
        }
        if (this.mCanAssumeUdfps) {
            return this.mRestoring;
        }
        return true;
    }

    private boolean isOkToEnroll() {
        return new LockPatternUtils(this).isSecure(this.mUserId);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        if (!isOkToEnroll()) {
            onEnrollmentError(-1, (CharSequence) null);
            return;
        }
        updateProgress(false);
        updateTitleAndDescription();
        if (this.mRestoring) {
            startIconAnimation();
        }
    }

    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        if (this.mCanAssumeUdfps) {
            startEnrollment();
        }
        this.mAnimationCancelled = false;
        startIconAnimation();
    }

    /* access modifiers changed from: private */
    public void startIconAnimation() {
        AnimatedVectorDrawable animatedVectorDrawable = this.mIconAnimationDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.start();
        }
    }

    private void stopIconAnimation() {
        this.mAnimationCancelled = true;
        AnimatedVectorDrawable animatedVectorDrawable = this.mIconAnimationDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.stop();
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        stopIconAnimation();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        stopListenOrientationEvent();
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public boolean isEnrollAbandonedOnBackPress() {
        if (getCallingActivity() != null) {
            return FingerprintSettings.class.equals(getCallingActivity().getClass());
        }
        return false;
    }

    private void animateProgress(int i) {
        if (!this.mCanAssumeUdfps) {
            ObjectAnimator objectAnimator = this.mProgressAnim;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            ProgressBar progressBar = this.mProgressBar;
            ObjectAnimator ofInt = ObjectAnimator.ofInt(progressBar, "progress", new int[]{progressBar.getProgress(), i});
            ofInt.addListener(this.mProgressAnimationListener);
            ofInt.setInterpolator(this.mFastOutSlowInInterpolator);
            ofInt.setDuration(250);
            ofInt.start();
            this.mProgressAnim = ofInt;
        } else if (i >= 10000) {
            getMainThreadHandler().postDelayed(this.mDelayedFinishRunnable, 250);
        }
    }

    private void animateFlash() {
        AnimatedVectorDrawable animatedVectorDrawable = this.mIconBackgroundBlinksDrawable;
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.start();
        }
    }

    /* access modifiers changed from: protected */
    public void launchFinish(byte[] bArr) {
        this.mIsNotAbandon = true;
        MotoSurveyUtils.sendSurveyIntent(this, "success");
        super.launchFinish(bArr);
    }

    /* access modifiers changed from: protected */
    public Intent getFinishIntent() {
        Intent intent;
        boolean booleanExtra = getIntent().getBooleanExtra("from_fingerprint_settings", false);
        if (FingerprintUtils.shouldShowTouchToUnlockSettings(this, this.mFromSettingsSummary, booleanExtra, false)) {
            intent = new Intent(this, FingerprintEnrollAlmostThere.class);
        } else {
            intent = new Intent(this, FingerprintEnrollFinish.class);
        }
        intent.putExtra("from_fingerprint_settings", booleanExtra);
        return intent;
    }

    private void updateTitleAndDescription() {
        BiometricEnrollSidecar biometricEnrollSidecar = this.mSidecar;
        if (biometricEnrollSidecar == null || biometricEnrollSidecar.getEnrollmentSteps() == -1) {
            if (this.mCanAssumeUdfps) {
                getLayout().setHeaderText(C1992R$string.security_settings_fingerprint_enroll_udfps_title);
                setDescriptionText(C1992R$string.security_settings_udfps_enroll_start_message);
                String string = getString(C1992R$string.security_settings_udfps_enroll_a11y);
                getLayout().getHeaderTextView().setContentDescription(string);
                setTitle(string);
                return;
            }
            setDescriptionText(C1992R$string.security_settings_fingerprint_enroll_start_message);
        } else if (this.mCanAssumeUdfps && !isCenterEnrollmentComplete()) {
            if (this.mIsSetupWizard) {
                setHeaderText(C1992R$string.security_settings_udfps_enroll_title_one_more_time);
            } else {
                setHeaderText(C1992R$string.security_settings_fingerprint_enroll_repeat_title);
            }
            setDescriptionText(C1992R$string.security_settings_udfps_enroll_start_message);
        } else if (this.mCanAssumeUdfps) {
            setHeaderText(C1992R$string.security_settings_fingerprint_enroll_repeat_title);
            if (this.mIsAccessibilityEnabled) {
                setDescriptionText(C1992R$string.security_settings_udfps_enroll_repeat_a11y_message);
            } else {
                setDescriptionText(C1992R$string.security_settings_udfps_enroll_repeat_message);
            }
        } else {
            int progress = getProgress(this.mSidecar.getEnrollmentSteps(), this.mSidecar.getEnrollmentRemaining());
            if (this.mIsMotoFODEnroll) {
                if (progress >= 4500) {
                    setDescriptionText(C1992R$string.security_settings_fingerprint_enroll_rotate_finger_message);
                } else if (progress >= 3500) {
                    setHeaderText(C1992R$string.fingerprint_enrolling_repeat_title_post_progress_threshold_two);
                }
            } else if (progress > 5000) {
                setHeaderText(C1992R$string.fingerprint_enrolling_repeat_title_post_progress_threshold_two);
            } else if (progress > 2500) {
                setHeaderText(C1992R$string.fingerprint_enrolling_repeat_title_post_progress_threshold_one);
            }
        }
    }

    private boolean isCenterEnrollmentComplete() {
        BiometricEnrollSidecar biometricEnrollSidecar = this.mSidecar;
        if (biometricEnrollSidecar == null || biometricEnrollSidecar.getEnrollmentSteps() == -1 || this.mSidecar.getEnrollmentSteps() - this.mSidecar.getEnrollmentRemaining() < 2) {
            return false;
        }
        return true;
    }

    public void onEnrollmentHelp(int i, CharSequence charSequence) {
        if (!TextUtils.isEmpty(charSequence)) {
            if (!this.mCanAssumeUdfps) {
                this.mErrorText.removeCallbacks(this.mTouchAgainRunnable);
            }
            showError(charSequence);
        }
    }

    public void onEnrollmentError(int i, CharSequence charSequence) {
        FingerprintErrorDialog.showErrorDialog(this, i);
        stopIconAnimation();
        if (!this.mCanAssumeUdfps) {
            this.mErrorText.removeCallbacks(this.mTouchAgainRunnable);
        }
    }

    public void onEnrollmentProgressChange(int i, int i2) {
        updateProgress(true);
        updateTitleAndDescription();
        clearError();
        animateFlash();
        if (!this.mCanAssumeUdfps) {
            this.mErrorText.removeCallbacks(this.mTouchAgainRunnable);
            this.mErrorText.postDelayed(this.mTouchAgainRunnable, 2500);
        } else if (this.mIsAccessibilityEnabled) {
            int i3 = (int) ((((float) (i - i2)) / ((float) i)) * 100.0f);
            String string = getString(C1992R$string.security_settings_udfps_enroll_progress_a11y_message, new Object[]{Integer.valueOf(i3)});
            AccessibilityEvent obtain = AccessibilityEvent.obtain();
            obtain.setEventType(16384);
            obtain.setClassName(getClass().getName());
            obtain.setPackageName(getPackageName());
            obtain.getText().add(string);
            this.mAccessibilityManager.sendAccessibilityEvent(obtain);
        }
    }

    private void updateProgress(boolean z) {
        BiometricEnrollSidecar biometricEnrollSidecar = this.mSidecar;
        if (biometricEnrollSidecar == null || !biometricEnrollSidecar.isEnrolling()) {
            Log.d("FingerprintEnrollEnrolling", "Enrollment not started yet");
            return;
        }
        int progress = getProgress(this.mSidecar.getEnrollmentSteps(), this.mSidecar.getEnrollmentRemaining());
        int enrollmentSteps = this.mSidecar.getEnrollmentSteps();
        int enrollmentRemaining = this.mSidecar.getEnrollmentRemaining();
        this.mProgress = progress;
        if (z) {
            animateProgress(progress);
            MotoLottieAnimView motoLottieAnimView = this.mMotoLottieAnimView;
            if (motoLottieAnimView != null) {
                motoLottieAnimView.updateProgress(enrollmentSteps, enrollmentRemaining);
            }
        } else {
            ProgressBar progressBar = this.mProgressBar;
            if (progressBar != null) {
                progressBar.setProgress(progress);
            }
            if (progress >= 10000) {
                this.mDelayedFinishRunnable.run();
            }
        }
        setFillInBackgroundState(progress);
        int i = enrollmentSteps + 1;
        setFillInAnimationState(z, i, i - enrollmentRemaining);
    }

    private int getProgress(int i, int i2) {
        if (i == -1) {
            return 0;
        }
        int i3 = i + 1;
        return (Math.max(0, i3 - i2) * 10000) / i3;
    }

    /* access modifiers changed from: private */
    public void showIconTouchDialog() {
        this.mIconTouchCount = 0;
        new IconTouchDialog().show(getSupportFragmentManager(), (String) null);
    }

    /* access modifiers changed from: private */
    public void showError(CharSequence charSequence) {
        if (this.mCanAssumeUdfps) {
            setHeaderText(charSequence);
            setDescriptionText((CharSequence) "");
        } else {
            this.mErrorText.setText(charSequence);
            if (this.mErrorText.getVisibility() == 4) {
                this.mErrorText.setVisibility(0);
                this.mErrorText.setTranslationY((float) getResources().getDimensionPixelSize(C1982R$dimen.fingerprint_error_text_appear_distance));
                this.mErrorText.setAlpha(0.0f);
                this.mErrorText.animate().alpha(1.0f).translationY(0.0f).setDuration(200).setInterpolator(this.mLinearOutSlowInInterpolator).start();
            } else {
                this.mErrorText.animate().cancel();
                this.mErrorText.setAlpha(1.0f);
                this.mErrorText.setTranslationY(0.0f);
            }
        }
        if (isResumed()) {
            this.mVibrator.vibrate(VIBRATE_EFFECT_ERROR, FINGERPRINT_ENROLLING_SONFICATION_ATTRIBUTES);
        }
    }

    private void clearError() {
        if (!this.mCanAssumeUdfps && this.mErrorText.getVisibility() == 0) {
            this.mErrorText.animate().alpha(0.0f).translationY((float) getResources().getDimensionPixelSize(C1982R$dimen.fingerprint_error_text_disappear_distance)).setDuration(100).setInterpolator(this.mFastOutLinearInInterpolator).withEndAction(new FingerprintEnrollEnrolling$$ExternalSyntheticLambda2(this)).start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearError$2() {
        this.mErrorText.setVisibility(4);
    }

    private void listenOrientationEvent() {
        C07541 r0 = new OrientationEventListener(this) {
            public void onOrientationChanged(int i) {
                int rotation = FingerprintEnrollEnrolling.this.getDisplay().getRotation();
                if ((FingerprintEnrollEnrolling.this.mPreviousRotation == 1 && rotation == 3) || (FingerprintEnrollEnrolling.this.mPreviousRotation == 3 && rotation == 1)) {
                    int unused = FingerprintEnrollEnrolling.this.mPreviousRotation = rotation;
                    FingerprintEnrollEnrolling.this.recreate();
                }
            }
        };
        this.mOrientationEventListener = r0;
        r0.enable();
        this.mPreviousRotation = getDisplay().getRotation();
    }

    private void stopListenOrientationEvent() {
        OrientationEventListener orientationEventListener = this.mOrientationEventListener;
        if (orientationEventListener != null) {
            orientationEventListener.disable();
        }
        this.mOrientationEventListener = null;
    }

    public static class IconTouchDialog extends InstrumentedDialogFragment {
        public int getMetricsCategory() {
            return 568;
        }

        public Dialog onCreateDialog(Bundle bundle) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(C1992R$string.security_settings_fingerprint_enroll_touch_dialog_title).setMessage(C1992R$string.security_settings_fingerprint_enroll_touch_dialog_message).setPositiveButton(C1992R$string.security_settings_fingerprint_enroll_dialog_got_it, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            return builder.create();
        }
    }

    private void setFillInBackgroundState(int i) {
        LevelListDrawable levelListDrawable = this.mFillInBackgroundDrawable;
        if (levelListDrawable != null) {
            levelListDrawable.setLevel(i);
        }
    }

    private void setFillInAnimationState(boolean z, int i, int i2) {
        AnimatedStateListDrawable animatedStateListDrawable = this.mFillInAnimationDrawable;
        if (animatedStateListDrawable != null && i > 0) {
            if (z) {
                int i3 = i + 1;
                int[] iArr = new int[i3];
                for (int i4 = 0; i4 < i3; i4++) {
                    if (i4 == i2) {
                        iArr[i4] = FINGERPRINT_FILL_ANIM_STATES[i4];
                    } else {
                        iArr[i4] = -FINGERPRINT_FILL_ANIM_STATES[i4];
                    }
                }
                if (this.mFillInAnimationDrawable.setState(iArr)) {
                    this.mFillInAnimationDrawable.invalidateSelf();
                    this.mFillInAnimationState = iArr;
                }
            } else if (animatedStateListDrawable.setState(this.mFillInAnimationState)) {
                this.mFillInAnimationDrawable.jumpToCurrentState();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: updateUiState */
    public void lambda$onCreate$1(boolean z) {
        if (this.mSidecar != null && this.mProgress != 0) {
            updateProgress(false);
            clearError();
            this.mErrorText.removeCallbacks(this.mTouchAgainRunnable);
            if (z) {
                this.mErrorText.postDelayed(this.mTouchAgainRunnable, 2500);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void initViews() {
        FingerprintUtils.overrideFpEnrollSuwHeaderTitleSizeIfNecessary(this);
        if (this.mIsMotoFODEnroll) {
            FingerprintUtils.applyFODTheme(this);
        }
    }

    /* access modifiers changed from: protected */
    public int getBackgroundColor() {
        if (this.mIsMotoFODEnroll) {
            return getResources().getColor(C1981R$color.fod_fingerprint_background_color, (Resources.Theme) null);
        }
        return super.getBackgroundColor();
    }
}

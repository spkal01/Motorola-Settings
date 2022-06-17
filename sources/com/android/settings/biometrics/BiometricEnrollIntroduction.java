package com.android.settings.biometrics;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C1977R$anim;
import com.android.settings.C1992R$string;
import com.android.settings.SetupWizardUtils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.span.LinkSpan;
import com.google.android.setupdesign.template.RequireScrollMixin;
import com.google.android.setupdesign.util.DynamicColorPalette;

public abstract class BiometricEnrollIntroduction extends BiometricEnrollBase implements LinkSpan.OnClickListener {
    private boolean mBiometricUnlockDisabledByAdmin;
    protected boolean mConfirmingCredentials;
    private TextView mErrorText;
    protected boolean mHasPassword;
    private PorterDuffColorFilter mIconColorFilter;
    protected boolean mNextClicked;
    private boolean mParentalConsentRequired;
    private UserManager mUserManager;

    protected interface GenerateChallengeCallback {
        void onChallengeGenerated(int i, int i2, long j);
    }

    private static boolean isResultSkipOrFinished(int i) {
        return i == 2 || i == 11 || i == 1;
    }

    /* access modifiers changed from: protected */
    public abstract int checkMaxEnrolled();

    /* access modifiers changed from: protected */
    public abstract int getAgreeButtonTextRes();

    /* access modifiers changed from: protected */
    public abstract void getChallenge(GenerateChallengeCallback generateChallengeCallback);

    /* access modifiers changed from: protected */
    public abstract int getConfirmLockTitleResId();

    /* access modifiers changed from: protected */
    public abstract int getDescriptionResDisabledByAdmin();

    /* access modifiers changed from: protected */
    public abstract Intent getEnrollingIntent();

    /* access modifiers changed from: protected */
    public abstract TextView getErrorTextView();

    /* access modifiers changed from: protected */
    public abstract String getExtraKeyForBiometric();

    /* access modifiers changed from: protected */
    public abstract int getHeaderResDefault();

    /* access modifiers changed from: protected */
    public abstract int getHeaderResDisabledByAdmin();

    /* access modifiers changed from: protected */
    public abstract int getLayoutResource();

    public abstract int getModality();

    /* access modifiers changed from: protected */
    public abstract int getMoreButtonTextRes();

    /* access modifiers changed from: protected */
    public abstract FooterButton getNextButton();

    /* access modifiers changed from: protected */
    public abstract FooterButton getPrimaryFooterButton();

    /* access modifiers changed from: protected */
    public abstract FooterButton getSecondaryFooterButton();

    /* access modifiers changed from: protected */
    public abstract boolean isDisabledByAdmin();

    /* access modifiers changed from: protected */
    public boolean isEnrollAbandonedOnBackPress() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean onSetOrConfirmCredentials(Intent intent) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mConfirmingCredentials = bundle.getBoolean("confirming_credentials");
        }
        Intent intent = getIntent();
        if (intent.getStringExtra("theme") == null) {
            intent.putExtra("theme", SetupWizardUtils.getThemeString(intent));
        }
        this.mBiometricUnlockDisabledByAdmin = isDisabledByAdmin();
        setContentView(getLayoutResource());
        boolean z = ParentalControlsUtils.parentConsentRequired(this, getModality()) != null;
        this.mParentalConsentRequired = z;
        if (!this.mBiometricUnlockDisabledByAdmin || z) {
            setHeaderText(getHeaderResDefault());
        } else {
            setHeaderText(getHeaderResDisabledByAdmin());
        }
        this.mErrorText = getErrorTextView();
        this.mUserManager = UserManager.get(this);
        updatePasswordQuality();
        if (!this.mConfirmingCredentials) {
            if (!this.mHasPassword) {
                this.mConfirmingCredentials = true;
                launchChooseLock();
            } else if (!BiometricUtils.containsGatekeeperPasswordHandle(getIntent()) && this.mToken == null) {
                this.mConfirmingCredentials = true;
                launchConfirmLock(getConfirmLockTitleResId());
            }
        }
        GlifLayout layout = getLayout();
        FooterBarMixin footerBarMixin = (FooterBarMixin) layout.getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        footerBarMixin.setPrimaryButton(getPrimaryFooterButton());
        this.mFooterBarMixin.setSecondaryButton(getSecondaryFooterButton());
        this.mFooterBarMixin.getSecondaryButton().setVisibility(0);
        RequireScrollMixin requireScrollMixin = (RequireScrollMixin) layout.getMixin(RequireScrollMixin.class);
        requireScrollMixin.requireScrollWithButton(this, getPrimaryFooterButton(), getMoreButtonTextRes(), new BiometricEnrollIntroduction$$ExternalSyntheticLambda0(this));
        requireScrollMixin.setOnRequireScrollStateChangedListener(new BiometricEnrollIntroduction$$ExternalSyntheticLambda3(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreate$0(boolean z) {
        int i;
        if (z) {
            i = getMoreButtonTextRes();
        } else {
            i = getAgreeButtonTextRes();
        }
        getPrimaryFooterButton().setText(this, i);
        if (!z) {
            getSecondaryFooterButton().setVisibility(0);
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        int checkMaxEnrolled = checkMaxEnrolled();
        if (checkMaxEnrolled == 0) {
            this.mErrorText.setText((CharSequence) null);
            this.mErrorText.setVisibility(8);
            getNextButton().setVisibility(0);
            return;
        }
        this.mErrorText.setText(checkMaxEnrolled);
        this.mErrorText.setVisibility(0);
        getNextButton().setText(getResources().getString(C1992R$string.done));
        getNextButton().setVisibility(0);
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("confirming_credentials", this.mConfirmingCredentials);
    }

    /* access modifiers changed from: protected */
    public boolean shouldFinishWhenBackgrounded() {
        return super.shouldFinishWhenBackgrounded() && !this.mConfirmingCredentials && !this.mNextClicked;
    }

    private void updatePasswordQuality() {
        this.mHasPassword = new LockPatternUtils(this).getActivePasswordQuality(this.mUserManager.getCredentialOwnerProfile(this.mUserId)) != 0;
    }

    /* access modifiers changed from: protected */
    public void onNextButtonClick(View view) {
        this.mNextClicked = true;
        if (checkMaxEnrolled() == 0) {
            launchNextEnrollingActivity(this.mToken);
            return;
        }
        setResult(1);
        finish();
    }

    private void launchChooseLock() {
        Intent chooseLockIntent = BiometricUtils.getChooseLockIntent(this, getIntent());
        chooseLockIntent.putExtra("hide_insecure_options", true);
        chooseLockIntent.putExtra("request_gk_pw_handle", true);
        chooseLockIntent.putExtra(getExtraKeyForBiometric(), true);
        int i = this.mUserId;
        if (i != -10000) {
            chooseLockIntent.putExtra("android.intent.extra.USER_ID", i);
        }
        startActivityForResult(chooseLockIntent, 1);
    }

    private void launchNextEnrollingActivity(byte[] bArr) {
        this.mIsNotAbandon = true;
        Intent enrollingIntent = getEnrollingIntent();
        if (bArr != null) {
            enrollingIntent.putExtra("hw_auth_token", bArr);
        }
        int i = this.mUserId;
        if (i != -10000) {
            enrollingIntent.putExtra("android.intent.extra.USER_ID", i);
        }
        BiometricUtils.copyMultiBiometricExtras(getIntent(), enrollingIntent);
        enrollingIntent.putExtra("from_settings_summary", this.mFromSettingsSummary);
        enrollingIntent.putExtra("challenge", this.mChallenge);
        enrollingIntent.putExtra("sensor_id", this.mSensorId);
        if (!isEnrollingFingerprint() || isTaskRoot() || WizardManagerHelper.isAnySetupWizard(getIntent())) {
            startActivityForResult(enrollingIntent, 2);
            return;
        }
        enrollingIntent.addFlags(268468224);
        startActivity(enrollingIntent);
        setResult(-1);
        finish();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 2) {
            if (isResultSkipOrFinished(i2)) {
                handleBiometricResultSkipOrFinished(i2, intent);
            } else if (i2 == 3) {
                setResult(i2, intent);
                finish();
            }
        } else if (i == 1) {
            this.mConfirmingCredentials = false;
            if (i2 == 1) {
                updatePasswordQuality();
                if (!onSetOrConfirmCredentials(intent)) {
                    overridePendingTransition(C1977R$anim.sud_slide_next_in, C1977R$anim.sud_slide_next_out);
                    getNextButton().setEnabled(false);
                    getChallenge(new BiometricEnrollIntroduction$$ExternalSyntheticLambda2(this, intent));
                }
            } else {
                setResult(i2, intent);
                finish();
            }
        } else if (i == 4) {
            this.mConfirmingCredentials = false;
            if (i2 != -1 || intent == null) {
                setResult(i2, intent);
                finish();
            } else if (!onSetOrConfirmCredentials(intent)) {
                overridePendingTransition(C1977R$anim.sud_slide_next_in, C1977R$anim.sud_slide_next_out);
                getNextButton().setEnabled(false);
                getChallenge(new BiometricEnrollIntroduction$$ExternalSyntheticLambda1(this, intent));
            }
        } else if (i == 3) {
            overridePendingTransition(C1977R$anim.sud_slide_back_in, C1977R$anim.sud_slide_back_out);
        } else if (i == 6) {
            Log.d("BiometricEnrollIntroduction", "ENROLL_NEXT_BIOMETRIC_REQUEST, result: " + i2);
            if (isResultSkipOrFinished(i2)) {
                handleBiometricResultSkipOrFinished(i2, intent);
            } else if (i2 != 0) {
                setResult(i2, intent);
                finish();
            }
        }
        super.onActivityResult(i, i2, intent);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActivityResult$1(Intent intent, int i, int i2, long j) {
        this.mSensorId = i;
        this.mChallenge = j;
        this.mToken = BiometricUtils.requestGatekeeperHat((Context) this, intent, this.mUserId, j);
        BiometricUtils.removeGatekeeperPasswordHandle((Context) this, intent);
        getNextButton().setEnabled(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActivityResult$2(Intent intent, int i, int i2, long j) {
        this.mSensorId = i;
        this.mChallenge = j;
        this.mToken = BiometricUtils.requestGatekeeperHat((Context) this, intent, this.mUserId, j);
        BiometricUtils.removeGatekeeperPasswordHandle((Context) this, intent);
        getNextButton().setEnabled(true);
    }

    private void handleBiometricResultSkipOrFinished(int i, Intent intent) {
        if (intent != null && intent.getBooleanExtra("skip_pending_enroll", false)) {
            getIntent().removeExtra("enroll_after_face");
        }
        if (i == 2) {
            onEnrollmentSkipped(intent);
        } else if (i == 1) {
            onFinishedEnrolling(intent);
        }
    }

    /* access modifiers changed from: protected */
    public void onSkipButtonClick(View view) {
        onEnrollmentSkipped((Intent) null);
    }

    /* access modifiers changed from: protected */
    public void onEnrollmentSkipped(Intent intent) {
        setResult(2, intent);
        finish();
    }

    /* access modifiers changed from: protected */
    public void onFinishedEnrolling(Intent intent) {
        setResult(1, intent);
        finish();
    }

    /* access modifiers changed from: protected */
    public void initViews() {
        super.initViews();
        if (this.mBiometricUnlockDisabledByAdmin && !this.mParentalConsentRequired) {
            setDescriptionText(getDescriptionResDisabledByAdmin());
        }
    }

    /* access modifiers changed from: protected */
    public PorterDuffColorFilter getIconColorFilter() {
        if (this.mIconColorFilter == null) {
            this.mIconColorFilter = new PorterDuffColorFilter(DynamicColorPalette.getColor(this, 0), PorterDuff.Mode.SRC_IN);
        }
        return this.mIconColorFilter;
    }
}

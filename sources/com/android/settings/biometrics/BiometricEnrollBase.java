package com.android.settings.biometrics;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.C1985R$id;
import com.android.settings.SetupWizardUtils;
import com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling;
import com.android.settings.core.InstrumentedActivity;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settingslib.Utils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.util.ThemeHelper;
import com.motorola.checkin.FingerprintEnrollmentCheckin;
import com.motorola.settingslib.displayutils.EndlessLayoutMixin;

public abstract class BiometricEnrollBase extends InstrumentedActivity {
    protected long mChallenge;
    protected FooterBarMixin mFooterBarMixin;
    protected boolean mFromSettingsSummary;
    protected boolean mIsNotAbandon;
    protected boolean mLaunchedConfirmLock;
    protected int mSensorId;
    protected byte[] mToken;
    protected int mUserId;

    /* access modifiers changed from: protected */
    public boolean isEnrollAbandonedOnBackPress() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isEnrollingFingerprint() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean trackAbandonAction() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getLifecycle().addObserver(new EndlessLayoutMixin(this));
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        ThemeHelper.trySetDynamicColor(this);
        this.mChallenge = getIntent().getLongExtra("challenge", -1);
        this.mSensorId = getIntent().getIntExtra("sensor_id", -1);
        if (this.mToken == null) {
            this.mToken = getIntent().getByteArrayExtra("hw_auth_token");
        }
        this.mFromSettingsSummary = getIntent().getBooleanExtra("from_settings_summary", false);
        if (bundle != null && this.mToken == null) {
            this.mLaunchedConfirmLock = bundle.getBoolean("launched_confirm_lock");
            this.mToken = bundle.getByteArray("hw_auth_token");
            this.mFromSettingsSummary = bundle.getBoolean("from_settings_summary", false);
            this.mChallenge = bundle.getLong("challenge");
            this.mSensorId = bundle.getInt("sensor_id");
        }
        this.mUserId = getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
        if (trackAbandonAction()) {
            FingerprintEnrollmentCheckin.startCheckin(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("launched_confirm_lock", this.mLaunchedConfirmLock);
        bundle.putByteArray("hw_auth_token", this.mToken);
        bundle.putBoolean("from_settings_summary", this.mFromSettingsSummary);
        bundle.putLong("challenge", this.mChallenge);
        bundle.putInt("sensor_id", this.mSensorId);
    }

    /* access modifiers changed from: protected */
    public void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
        initViews();
        FooterBarMixin footerBarMixin = this.mFooterBarMixin;
        LinearLayout buttonContainer = footerBarMixin != null ? footerBarMixin.getButtonContainer() : null;
        if (buttonContainer != null) {
            buttonContainer.setBackgroundColor(getBackgroundColor());
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().setStatusBarColor(getBackgroundColor());
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        if (!isChangingConfigurations() && shouldFinishWhenBackgrounded()) {
            setResult(3);
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.mIsNotAbandon = false;
    }

    /* access modifiers changed from: protected */
    public boolean shouldFinishWhenBackgrounded() {
        return !WizardManagerHelper.isAnySetupWizard(getIntent());
    }

    public void onBackPressed() {
        this.mIsNotAbandon = !isEnrollAbandonedOnBackPress();
        super.onBackPressed();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations() && trackAbandonAction() && !this.mIsNotAbandon) {
            FingerprintEnrollmentCheckin.logUserAbandonEvents(this, getClass().getSimpleName());
        }
    }

    /* access modifiers changed from: protected */
    public void initViews() {
        getWindow().setStatusBarColor(0);
    }

    /* access modifiers changed from: protected */
    public GlifLayout getLayout() {
        return (GlifLayout) findViewById(C1985R$id.setup_wizard_layout);
    }

    /* access modifiers changed from: protected */
    public void setHeaderText(int i, boolean z) {
        TextView headerTextView = getLayout().getHeaderTextView();
        CharSequence text = headerTextView.getText();
        CharSequence text2 = getText(i);
        if (text != text2 || z) {
            if (!TextUtils.isEmpty(text)) {
                headerTextView.setAccessibilityLiveRegion(1);
            }
            getLayout().setHeaderText(text2);
            getLayout().getHeaderTextView().setContentDescription(text2);
            setTitle(text2);
        }
    }

    /* access modifiers changed from: protected */
    public void setHeaderText(int i) {
        setHeaderText(i, false);
        getLayout().getHeaderTextView().setContentDescription(getText(i));
    }

    /* access modifiers changed from: protected */
    public void setHeaderText(CharSequence charSequence) {
        getLayout().setHeaderText(charSequence);
        getLayout().getHeaderTextView().setContentDescription(charSequence);
    }

    /* access modifiers changed from: protected */
    public void setDescriptionText(int i) {
        if (!TextUtils.equals(getLayout().getDescriptionText(), getString(i))) {
            getLayout().setDescriptionText(i);
        }
    }

    /* access modifiers changed from: protected */
    public void setDescriptionText(CharSequence charSequence) {
        getLayout().setDescriptionText(charSequence);
    }

    /* access modifiers changed from: protected */
    public Intent getFingerprintEnrollingIntent() {
        return getIntentForClassName(FingerprintEnrollEnrolling.class.getName());
    }

    /* access modifiers changed from: protected */
    public Intent getIntentForClassName(String str) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", str);
        intent.putExtra("hw_auth_token", this.mToken);
        intent.putExtra("from_settings_summary", this.mFromSettingsSummary);
        intent.putExtra("challenge", this.mChallenge);
        intent.putExtra("sensor_id", this.mSensorId);
        int i = this.mUserId;
        if (i != -10000) {
            intent.putExtra("android.intent.extra.USER_ID", i);
        }
        return intent;
    }

    /* access modifiers changed from: protected */
    public void launchConfirmLock(int i) {
        Log.d("BiometricEnrollBase", "launchConfirmLock");
        ChooseLockSettingsHelper.Builder builder = new ChooseLockSettingsHelper.Builder(this);
        builder.setRequestCode(4).setTitle(getString(i)).setRequestGatekeeperPasswordHandle(true).setForegroundOnly(true).setReturnCredentials(true);
        int i2 = this.mUserId;
        if (i2 != -10000) {
            builder.setUserId(i2);
        }
        if (!builder.show()) {
            finish();
        } else {
            this.mLaunchedConfirmLock = true;
        }
    }

    /* access modifiers changed from: protected */
    public int getBackgroundColor() {
        ColorStateList colorAttr = Utils.getColorAttr(this, 16842836);
        if (colorAttr != null) {
            return colorAttr.getDefaultColor();
        }
        return 0;
    }
}

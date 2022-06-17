package com.android.settings.password;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImeAwareEditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockscreenCredential;
import com.android.internal.widget.TextViewInputDisabler;
import com.android.internal.widget.VerifyCredentialResponse;
import com.android.settings.C1977R$anim;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.password.ConfirmDeviceCredentialBaseActivity;
import com.android.settings.password.CredentialCheckResultTracker;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.settingslib.animation.DisappearAnimationUtils;
import com.google.android.setupdesign.GlifLayout;
import com.motorola.settings.widget.PasswordView;
import java.util.ArrayList;

public class ConfirmLockPassword extends ConfirmDeviceCredentialBaseActivity {
    /* access modifiers changed from: private */
    public static final int[] DETAIL_TEXTS = {C1992R$string.lockpassword_confirm_your_pin_generic, C1992R$string.lockpassword_confirm_your_password_generic, C1992R$string.lockpassword_confirm_your_pin_generic_profile, C1992R$string.lockpassword_confirm_your_password_generic_profile, C1992R$string.lockpassword_strong_auth_required_device_pin, C1992R$string.lockpassword_strong_auth_required_device_password, C1992R$string.lockpassword_strong_auth_required_work_pin, C1992R$string.lockpassword_strong_auth_required_work_password};

    public static class InternalActivity extends ConfirmLockPassword {
    }

    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", ConfirmLockPasswordFragment.class.getName());
        return intent;
    }

    /* access modifiers changed from: protected */
    public boolean isValidFragment(String str) {
        return ConfirmLockPasswordFragment.class.getName().equals(str);
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        Fragment findFragmentById = getSupportFragmentManager().findFragmentById(C1985R$id.main_content);
        if (findFragmentById != null && (findFragmentById instanceof ConfirmLockPasswordFragment)) {
            ((ConfirmLockPasswordFragment) findFragmentById).onWindowFocusChanged(z);
        }
    }

    public static class ConfirmLockPasswordFragment extends ConfirmDeviceCredentialBaseFragment implements View.OnClickListener, TextView.OnEditorActionListener, CredentialCheckResultTracker.Listener {
        private AppearAnimationUtils mAppearAnimationUtils;
        private CountDownTimer mCountdownTimer;
        /* access modifiers changed from: private */
        public CredentialCheckResultTracker mCredentialCheckResultTracker;
        private TextViewInputDisabler mCurrentEntryInputDisabler;
        /* access modifiers changed from: private */
        public ImeAwareEditText mCurrentPasswordEntry;
        private DisappearAnimationUtils mDisappearAnimationUtils;
        private boolean mDisappearing = false;
        private GlifLayout mGlifLayout;
        private InputMethodManager mImm;
        /* access modifiers changed from: private */
        public boolean mIsAlpha;
        private boolean mIsCreatePrivacySpace = false;
        private boolean mIsManagedProfile;
        private ImeAwareEditText mPasswordEntry;
        private TextViewInputDisabler mPasswordEntryInputDisabler;
        /* access modifiers changed from: private */
        public AsyncTask<?, ?, ?> mPendingLockCheck;
        private PasswordView mSixBitEntry;
        private TextViewInputDisabler mSixBitEntryInputDisabler;
        /* access modifiers changed from: private */
        public TextChangedHandler mTextChangedHandler;

        public int getMetricsCategory() {
            return 30;
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            int i;
            int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mEffectiveUserId);
            if (((ConfirmLockPassword) getActivity()).getConfirmCredentialTheme() == ConfirmDeviceCredentialBaseActivity.ConfirmCredentialTheme.NORMAL) {
                i = C1987R$layout.confirm_lock_password_normal;
            } else {
                i = C1987R$layout.confirm_lock_password;
            }
            View inflate = layoutInflater.inflate(i, viewGroup, false);
            this.mGlifLayout = (GlifLayout) inflate.findViewById(C1985R$id.setup_wizard_layout);
            ImeAwareEditText findViewById = inflate.findViewById(C1985R$id.password_entry);
            this.mPasswordEntry = findViewById;
            findViewById.setOnEditorActionListener(this);
            this.mPasswordEntry.requestFocus();
            this.mPasswordEntryInputDisabler = new TextViewInputDisabler(this.mPasswordEntry);
            this.mErrorTextView = (TextView) inflate.findViewById(C1985R$id.errorText);
            this.mIsAlpha = 262144 == keyguardStoredPasswordQuality || 327680 == keyguardStoredPasswordQuality || 393216 == keyguardStoredPasswordQuality || 524288 == keyguardStoredPasswordQuality;
            this.mImm = (InputMethodManager) getActivity().getSystemService("input_method");
            this.mIsManagedProfile = UserManager.get(getActivity()).isManagedProfile(this.mEffectiveUserId);
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                this.mIsCreatePrivacySpace = intent.getBooleanExtra("setting_from_privacy_space", false);
                CharSequence charSequenceExtra = intent.getCharSequenceExtra("com.android.settings.ConfirmCredentials.header");
                CharSequence charSequenceExtra2 = intent.getCharSequenceExtra("com.android.settings.ConfirmCredentials.details");
                if (TextUtils.isEmpty(charSequenceExtra) && this.mIsManagedProfile) {
                    charSequenceExtra = this.mDevicePolicyManager.getOrganizationNameForUser(this.mUserId);
                }
                if (TextUtils.isEmpty(charSequenceExtra)) {
                    charSequenceExtra = getString(getDefaultHeader());
                }
                if (TextUtils.isEmpty(charSequenceExtra2)) {
                    charSequenceExtra2 = getString(getDefaultDetails());
                }
                this.mGlifLayout.setHeaderText(charSequenceExtra);
                this.mGlifLayout.setDescriptionText(charSequenceExtra2);
            }
            int inputType = this.mPasswordEntry.getInputType();
            if (this.mIsAlpha) {
                this.mPasswordEntry.setInputType(inputType);
                this.mPasswordEntry.setContentDescription(getContext().getString(C1992R$string.unlock_set_unlock_password_title));
            } else {
                this.mPasswordEntry.setInputType(18);
                this.mPasswordEntry.setContentDescription(getContext().getString(C1992R$string.unlock_set_unlock_pin_title));
            }
            this.mPasswordEntry.setTypeface(Typeface.create(getContext().getString(17039962), 0));
            this.mAppearAnimationUtils = new AppearAnimationUtils(getContext(), 220, 2.0f, 1.0f, AnimationUtils.loadInterpolator(getContext(), 17563662));
            this.mDisappearAnimationUtils = new DisappearAnimationUtils(getContext(), 110, 1.0f, 0.5f, AnimationUtils.loadInterpolator(getContext(), 17563663));
            setAccessibilityTitle(this.mGlifLayout.getHeaderText());
            CredentialCheckResultTracker credentialCheckResultTracker = (CredentialCheckResultTracker) getFragmentManager().findFragmentByTag("check_lock_result");
            this.mCredentialCheckResultTracker = credentialCheckResultTracker;
            if (credentialCheckResultTracker == null) {
                this.mCredentialCheckResultTracker = new CredentialCheckResultTracker();
                getFragmentManager().beginTransaction().add((Fragment) this.mCredentialCheckResultTracker, "check_lock_result").commit();
            }
            return inflate;
        }

        /* JADX WARNING: type inference failed for: r0v7, types: [android.widget.TextView, com.motorola.settings.widget.PasswordView] */
        private void initPrcContent(View view) {
            this.mTextChangedHandler = new TextChangedHandler();
            PasswordView passwordView = (PasswordView) view.findViewById(C1985R$id.password_entry_six_bit);
            this.mSixBitEntry = passwordView;
            passwordView.setOnEditorActionListener(this);
            this.mSixBitEntry.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable editable) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    ConfirmLockPasswordFragment.this.mTextChangedHandler.notifyAfterTextChanged();
                }
            });
            int inputType = this.mSixBitEntry.getInputType();
            PasswordView passwordView2 = this.mSixBitEntry;
            if (!this.mIsAlpha) {
                inputType = 18;
            }
            passwordView2.setInputType(inputType);
            if (this.mIsAlpha) {
                this.mSixBitEntry.setContentDescription(getString(C1992R$string.unlock_set_unlock_password_title));
            } else {
                this.mSixBitEntry.setContentDescription(getString(C1992R$string.unlock_set_unlock_pin_title));
            }
            this.mSixBitEntry.setTypeface(Typeface.create(getContext().getString(17039962), 0));
            this.mSixBitEntryInputDisabler = new TextViewInputDisabler(this.mSixBitEntry);
        }

        class TextChangedHandler extends Handler {
            TextChangedHandler() {
            }

            /* access modifiers changed from: private */
            public void notifyAfterTextChanged() {
                removeMessages(1);
                sendEmptyMessageDelayed(1, 100);
            }

            public void handleMessage(Message message) {
                LockscreenCredential lockscreenCredential;
                if (ConfirmLockPasswordFragment.this.getActivity() != null && message.what == 1) {
                    if (ConfirmLockPasswordFragment.this.mIsAlpha) {
                        lockscreenCredential = LockscreenCredential.createPasswordOrNone(ConfirmLockPasswordFragment.this.mCurrentPasswordEntry.getText());
                    } else {
                        lockscreenCredential = LockscreenCredential.createPinOrNone(ConfirmLockPasswordFragment.this.mCurrentPasswordEntry.getText());
                    }
                    if (lockscreenCredential.size() == 6) {
                        ConfirmLockPasswordFragment.this.handleNext();
                    }
                }
            }
        }

        public void onViewCreated(View view, Bundle bundle) {
            int i;
            super.onViewCreated(view, bundle);
            Button button = this.mForgotButton;
            if (button != null) {
                if (this.mIsAlpha) {
                    i = C1992R$string.lockpassword_forgot_password;
                } else {
                    i = C1992R$string.lockpassword_forgot_pin;
                }
                button.setText(i);
            }
            if (!Build.IS_PRC_PRODUCT || this.mIsAlpha || !this.mLockPatternUtils.isSixBitForPinLock(this.mUserId)) {
                this.mCurrentEntryInputDisabler = this.mPasswordEntryInputDisabler;
                this.mCurrentPasswordEntry = this.mPasswordEntry;
                return;
            }
            initPrcContent(view);
            this.mPasswordEntry.setVisibility(8);
            this.mSixBitEntry.setVisibility(0);
            this.mCurrentEntryInputDisabler = this.mSixBitEntryInputDisabler;
            this.mCurrentPasswordEntry = this.mSixBitEntry;
        }

        public void onDestroy() {
            super.onDestroy();
            this.mCurrentPasswordEntry.setText((CharSequence) null);
            new Handler(Looper.myLooper()).postDelayed(C1239x97dc6ea.INSTANCE, 5000);
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$onDestroy$0() {
            System.gc();
            System.runFinalization();
            System.gc();
        }

        private int getDefaultHeader() {
            if (this.mFrp) {
                if (this.mIsAlpha) {
                    return C1992R$string.lockpassword_confirm_your_password_header_frp;
                }
                return C1992R$string.lockpassword_confirm_your_pin_header_frp;
            } else if (this.mIsManagedProfile) {
                if (this.mIsAlpha) {
                    return C1992R$string.lockpassword_confirm_your_work_password_header;
                }
                return C1992R$string.lockpassword_confirm_your_work_pin_header;
            } else if (this.mIsPrivacyUser) {
                if (this.mIsAlpha) {
                    return C1992R$string.lockpassword_confirm_your_privacy_space_password_header;
                }
                return C1992R$string.lockpassword_confirm_your_privacy_space_pin_header;
            } else if (this.mIsCreatePrivacySpace) {
                if (this.mIsAlpha) {
                    return C1992R$string.lockpassword_confirm_your_main_space_password_header;
                }
                return C1992R$string.lockpassword_confirm_your_main_space_pin_header;
            } else if (this.mIsAlpha) {
                return C1992R$string.lockpassword_confirm_your_password_header;
            } else {
                return C1992R$string.lockpassword_confirm_your_pin_header;
            }
        }

        private int getDefaultDetails() {
            if (!this.mFrp) {
                return ConfirmLockPassword.DETAIL_TEXTS[((isStrongAuthRequired() ? 1 : 0) << true) + ((this.mIsManagedProfile ? 1 : 0) << true) + (this.mIsAlpha ? 1 : 0)];
            } else if (this.mIsAlpha) {
                return C1992R$string.lockpassword_confirm_your_password_details_frp;
            } else {
                return C1992R$string.lockpassword_confirm_your_pin_details_frp;
            }
        }

        private int getErrorMessage() {
            if (this.mIsAlpha) {
                return C1992R$string.lockpassword_invalid_password;
            }
            return C1992R$string.lockpassword_invalid_pin;
        }

        /* access modifiers changed from: protected */
        public int getLastTryErrorMessage(int i) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        throw new IllegalArgumentException("Unrecognized user type:" + i);
                    } else if (this.mIsAlpha) {
                        return C1992R$string.lock_last_password_attempt_before_wipe_user;
                    } else {
                        return C1992R$string.lock_last_pin_attempt_before_wipe_user;
                    }
                } else if (this.mIsAlpha) {
                    return C1992R$string.lock_last_password_attempt_before_wipe_profile;
                } else {
                    return C1992R$string.lock_last_pin_attempt_before_wipe_profile;
                }
            } else if (this.mIsAlpha) {
                return C1992R$string.lock_last_password_attempt_before_wipe_device;
            } else {
                return C1992R$string.lock_last_pin_attempt_before_wipe_device;
            }
        }

        public void prepareEnterAnimation() {
            super.prepareEnterAnimation();
            this.mGlifLayout.getHeaderTextView().setAlpha(0.0f);
            this.mGlifLayout.getDescriptionTextView().setAlpha(0.0f);
            this.mCancelButton.setAlpha(0.0f);
            Button button = this.mForgotButton;
            if (button != null) {
                button.setAlpha(0.0f);
            }
            this.mCurrentPasswordEntry.setAlpha(0.0f);
            this.mErrorTextView.setAlpha(0.0f);
        }

        private View[] getActiveViews() {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.mGlifLayout.getHeaderTextView());
            arrayList.add(this.mGlifLayout.getDescriptionTextView());
            if (this.mCancelButton.getVisibility() == 0) {
                arrayList.add(this.mCancelButton);
            }
            Button button = this.mForgotButton;
            if (button != null) {
                arrayList.add(button);
            }
            arrayList.add(this.mCurrentPasswordEntry);
            arrayList.add(this.mErrorTextView);
            return (View[]) arrayList.toArray(new View[0]);
        }

        public void startEnterAnimation() {
            super.startEnterAnimation();
            this.mAppearAnimationUtils.startAnimation(getActiveViews(), new C1237x97dc6e8(this));
        }

        public void onPause() {
            super.onPause();
            CountDownTimer countDownTimer = this.mCountdownTimer;
            if (countDownTimer != null) {
                countDownTimer.cancel();
                this.mCountdownTimer = null;
            }
            this.mCredentialCheckResultTracker.setListener((CredentialCheckResultTracker.Listener) null);
        }

        public void onResume() {
            super.onResume();
            this.mCurrentPasswordEntry.requestFocus();
            this.mCurrentPasswordEntry.scheduleShowSoftInput();
            long lockoutAttemptDeadline = this.mLockPatternUtils.getLockoutAttemptDeadline(this.mEffectiveUserId);
            if (lockoutAttemptDeadline != 0) {
                this.mCredentialCheckResultTracker.clearResult();
                handleAttemptLockout(lockoutAttemptDeadline);
            } else {
                updatePasswordEntry();
                this.mErrorTextView.setText("");
                updateErrorMessage(this.mLockPatternUtils.getCurrentFailedPasswordAttempts(this.mEffectiveUserId));
            }
            this.mCredentialCheckResultTracker.setListener(this);
        }

        /* access modifiers changed from: private */
        public void updatePasswordEntry() {
            boolean z = this.mLockPatternUtils.getLockoutAttemptDeadline(this.mEffectiveUserId) != 0;
            this.mCurrentPasswordEntry.setEnabled(!z);
            this.mCurrentEntryInputDisabler.setInputEnabled(!z);
            if (z) {
                this.mImm.hideSoftInputFromWindow(this.mCurrentPasswordEntry.getWindowToken(), 0);
            } else {
                this.mCurrentPasswordEntry.scheduleShowSoftInput();
            }
        }

        public void onWindowFocusChanged(boolean z) {
            if (z) {
                this.mCurrentPasswordEntry.post(new C1237x97dc6e8(this));
            }
        }

        /* access modifiers changed from: private */
        public void handleNext() {
            LockscreenCredential lockscreenCredential;
            if (this.mPendingLockCheck == null && !this.mDisappearing) {
                Editable text = this.mCurrentPasswordEntry.getText();
                if (!TextUtils.isEmpty(text)) {
                    if (this.mIsAlpha) {
                        lockscreenCredential = LockscreenCredential.createPassword(text);
                    } else {
                        lockscreenCredential = LockscreenCredential.createPin(text);
                    }
                    this.mCurrentEntryInputDisabler.setInputEnabled(false);
                    Intent intent = new Intent();
                    if (this.mReturnGatekeeperPassword) {
                        if (isInternalActivity()) {
                            startVerifyPassword(lockscreenCredential, intent, 1);
                            return;
                        }
                    } else if (!this.mForceVerifyPath) {
                        startCheckPassword(lockscreenCredential, intent);
                        return;
                    } else if (isInternalActivity()) {
                        startVerifyPassword(lockscreenCredential, intent, 0);
                        return;
                    }
                    this.mCredentialCheckResultTracker.setResult(false, intent, 0, this.mEffectiveUserId);
                }
            }
        }

        /* access modifiers changed from: private */
        public boolean isInternalActivity() {
            return getActivity() instanceof InternalActivity;
        }

        private void startVerifyPassword(LockscreenCredential lockscreenCredential, Intent intent, int i) {
            AsyncTask<?, ?, ?> asyncTask;
            int i2 = this.mEffectiveUserId;
            int i3 = this.mUserId;
            C1236x97dc6e7 confirmLockPassword$ConfirmLockPasswordFragment$$ExternalSyntheticLambda0 = new C1236x97dc6e7(this, i, intent, i2);
            if (i2 == i3) {
                asyncTask = LockPatternChecker.verifyCredential(this.mLockPatternUtils, lockscreenCredential, i3, i, confirmLockPassword$ConfirmLockPasswordFragment$$ExternalSyntheticLambda0);
            } else {
                asyncTask = LockPatternChecker.verifyTiedProfileChallenge(this.mLockPatternUtils, lockscreenCredential, i3, i, confirmLockPassword$ConfirmLockPasswordFragment$$ExternalSyntheticLambda0);
            }
            this.mPendingLockCheck = asyncTask;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$startVerifyPassword$1(int i, Intent intent, int i2, VerifyCredentialResponse verifyCredentialResponse, int i3) {
            this.mPendingLockCheck = null;
            boolean isMatched = verifyCredentialResponse.isMatched();
            if (isMatched && this.mReturnCredentials) {
                if ((i & 1) != 0) {
                    intent.putExtra("gk_pw_handle", verifyCredentialResponse.getGatekeeperPasswordHandle());
                } else {
                    intent.putExtra("hw_auth_token", verifyCredentialResponse.getGatekeeperHAT());
                }
            }
            this.mCredentialCheckResultTracker.setResult(isMatched, intent, i3, i2);
        }

        private void startCheckPassword(final LockscreenCredential lockscreenCredential, final Intent intent) {
            final int i = this.mEffectiveUserId;
            this.mPendingLockCheck = LockPatternChecker.checkCredential(this.mLockPatternUtils, lockscreenCredential, i, new LockPatternChecker.OnCheckCallback() {
                public void onChecked(boolean z, int i) {
                    AsyncTask unused = ConfirmLockPasswordFragment.this.mPendingLockCheck = null;
                    if (z && ConfirmLockPasswordFragment.this.isInternalActivity()) {
                        ConfirmLockPasswordFragment confirmLockPasswordFragment = ConfirmLockPasswordFragment.this;
                        if (confirmLockPasswordFragment.mReturnCredentials) {
                            intent.putExtra("type", confirmLockPasswordFragment.mIsAlpha ? 0 : 3);
                            intent.putExtra("password", lockscreenCredential);
                        }
                    }
                    ConfirmLockPasswordFragment.this.mCredentialCheckResultTracker.setResult(z, intent, i, i);
                }
            });
        }

        private void startDisappearAnimation(Intent intent) {
            if (!this.mDisappearing) {
                this.mDisappearing = true;
                ConfirmLockPassword confirmLockPassword = (ConfirmLockPassword) getActivity();
                if (confirmLockPassword != null && !confirmLockPassword.isFinishing()) {
                    if (confirmLockPassword.getConfirmCredentialTheme() == ConfirmDeviceCredentialBaseActivity.ConfirmCredentialTheme.DARK) {
                        this.mDisappearAnimationUtils.startAnimation(getActiveViews(), new C1238x97dc6e9(confirmLockPassword, intent));
                        return;
                    }
                    confirmLockPassword.setResult(-1, intent);
                    confirmLockPassword.finish();
                }
            }
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$startDisappearAnimation$2(ConfirmLockPassword confirmLockPassword, Intent intent) {
            confirmLockPassword.setResult(-1, intent);
            confirmLockPassword.finish();
            confirmLockPassword.overridePendingTransition(C1977R$anim.confirm_credential_close_enter, C1977R$anim.confirm_credential_close_exit);
        }

        private void onPasswordChecked(boolean z, Intent intent, int i, int i2, boolean z2) {
            this.mCurrentEntryInputDisabler.setInputEnabled(true);
            if (z) {
                if (z2) {
                    ConfirmDeviceCredentialUtils.reportSuccessfulAttempt(this.mLockPatternUtils, this.mUserManager, this.mDevicePolicyManager, this.mEffectiveUserId, true);
                }
                startDisappearAnimation(intent);
                ConfirmDeviceCredentialUtils.checkForPendingIntent(getActivity());
                return;
            }
            if (Build.IS_PRC_PRODUCT && !this.mIsAlpha && this.mLockPatternUtils.isSixBitForPinLock(this.mUserId)) {
                this.mSixBitEntry.resetPasswordText();
            }
            if (i > 0) {
                refreshLockScreen();
                handleAttemptLockout(this.mLockPatternUtils.setLockoutAttemptDeadline(i2, i));
            } else {
                showError(getErrorMessage(), 3000);
            }
            if (z2) {
                reportFailedAttempt();
            }
        }

        public void onCredentialChecked(boolean z, Intent intent, int i, int i2, boolean z2) {
            onPasswordChecked(z, intent, i, i2, z2);
        }

        /* access modifiers changed from: protected */
        public void onShowError() {
            this.mCurrentPasswordEntry.setText((CharSequence) null);
        }

        private void handleAttemptLockout(long j) {
            this.mCountdownTimer = new CountDownTimer(j - SystemClock.elapsedRealtime(), 1000) {
                public void onTick(long j) {
                    ConfirmLockPasswordFragment confirmLockPasswordFragment = ConfirmLockPasswordFragment.this;
                    confirmLockPasswordFragment.showError((CharSequence) confirmLockPasswordFragment.getString(C1992R$string.lockpattern_too_many_failed_confirmation_attempts, Integer.valueOf((int) (j / 1000))), 0);
                }

                public void onFinish() {
                    ConfirmLockPasswordFragment.this.updatePasswordEntry();
                    ConfirmLockPasswordFragment.this.mErrorTextView.setText("");
                    ConfirmLockPasswordFragment confirmLockPasswordFragment = ConfirmLockPasswordFragment.this;
                    confirmLockPasswordFragment.updateErrorMessage(confirmLockPasswordFragment.mLockPatternUtils.getCurrentFailedPasswordAttempts(confirmLockPasswordFragment.mEffectiveUserId));
                }
            }.start();
            updatePasswordEntry();
        }

        public void onClick(View view) {
            if (view.getId() == C1985R$id.next_button) {
                handleNext();
            } else if (view.getId() == C1985R$id.cancel_button) {
                getActivity().setResult(0);
                getActivity().finish();
            }
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 0 && i != 6 && i != 5) {
                return false;
            }
            handleNext();
            return true;
        }
    }
}

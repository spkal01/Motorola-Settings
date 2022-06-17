package com.motorola.settings.security.screenlock;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.constraintlayout.widget.R$styleable;
import androidx.fragment.app.Fragment;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternView;
import com.android.internal.widget.LockscreenCredential;
import com.android.settings.C1979R$attr;
import com.android.settings.C1980R$bool;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import com.android.settings.SetupWizardUtils;
import com.android.settings.Utils;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.password.SaveChosenLockWorkerBase;
import com.google.android.collect.Lists;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.template.IconMixin;
import com.google.android.setupdesign.util.ThemeHelper;
import java.util.Collections;
import java.util.List;

public class PrivacySpaceChooseLockPattern extends SettingsSetUpActivity {
    /* access modifiers changed from: protected */
    public boolean isToolbarEnabled() {
        return false;
    }

    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", getFragmentClass().getName());
        return intent;
    }

    public static class IntentBuilder {
        private final Intent mIntent;

        public IntentBuilder(Context context) {
            Intent intent = new Intent(context, PrivacySpaceChooseLockPattern.class);
            this.mIntent = intent;
            intent.putExtra("extra_require_password", false);
            intent.putExtra("confirm_credentials", false);
        }

        public IntentBuilder setForFingerprint(boolean z) {
            this.mIntent.putExtra("for_fingerprint", z);
            return this;
        }

        public IntentBuilder setMainUserPassword(LockscreenCredential lockscreenCredential) {
            this.mIntent.putExtra("main_space_password", lockscreenCredential);
            return this;
        }

        public IntentBuilder setMainUserCurrentPwd(LockscreenCredential lockscreenCredential) {
            this.mIntent.putExtra("password", lockscreenCredential);
            return this;
        }

        public IntentBuilder setPrivacyUserCurrentPwd(LockscreenCredential lockscreenCredential) {
            this.mIntent.putExtra("privacy_space_current_password", lockscreenCredential);
            return this;
        }

        public IntentBuilder setIsCreatePrivacySpace(boolean z) {
            this.mIntent.putExtra("setting_from_privacy_space", z);
            return this;
        }

        public Intent build() {
            return this.mIntent;
        }
    }

    /* access modifiers changed from: protected */
    public boolean isValidFragment(String str) {
        return PrivacySpaceChooseLockPatternFragment.class.getName().equals(str);
    }

    /* access modifiers changed from: package-private */
    public Class<? extends Fragment> getFragmentClass() {
        return PrivacySpaceChooseLockPatternFragment.class;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        ThemeHelper.trySetDynamicColor(this);
        super.onCreate(bundle);
        findViewById(C1985R$id.content_parent).setFitsSystemWindows(false);
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return super.onKeyDown(i, keyEvent);
    }

    public static class PrivacySpaceChooseLockPatternFragment extends InstrumentedFragment implements SaveChosenLockWorkerBase.Listener {
        private final List<LockPatternView.Cell> mAnimatePattern = Collections.unmodifiableList(Lists.newArrayList(new LockPatternView.Cell[]{LockPatternView.Cell.of(0, 0), LockPatternView.Cell.of(0, 1), LockPatternView.Cell.of(1, 1), LockPatternView.Cell.of(2, 1)}));
        protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {
            public void onPatternCellAdded(List<LockPatternView.Cell> list) {
            }

            public void onPatternStart() {
                PrivacySpaceChooseLockPatternFragment privacySpaceChooseLockPatternFragment = PrivacySpaceChooseLockPatternFragment.this;
                privacySpaceChooseLockPatternFragment.mLockPatternView.removeCallbacks(privacySpaceChooseLockPatternFragment.mClearPatternRunnable);
                patternInProgress();
            }

            public void onPatternCleared() {
                PrivacySpaceChooseLockPatternFragment privacySpaceChooseLockPatternFragment = PrivacySpaceChooseLockPatternFragment.this;
                privacySpaceChooseLockPatternFragment.mLockPatternView.removeCallbacks(privacySpaceChooseLockPatternFragment.mClearPatternRunnable);
            }

            public void onPatternDetected(List<LockPatternView.Cell> list) {
                if (PrivacySpaceChooseLockPatternFragment.this.mUiStage == Stage.NeedToConfirm || PrivacySpaceChooseLockPatternFragment.this.mUiStage == Stage.ConfirmWrong) {
                    if (PrivacySpaceChooseLockPatternFragment.this.mChosenPattern != null) {
                        LockscreenCredential createPattern = LockscreenCredential.createPattern(list);
                        try {
                            if (PrivacySpaceChooseLockPatternFragment.this.mChosenPattern.equals(createPattern)) {
                                PrivacySpaceChooseLockPatternFragment.this.updateStage(Stage.ChoiceConfirmed);
                            } else {
                                PrivacySpaceChooseLockPatternFragment.this.updateStage(Stage.ConfirmWrong);
                            }
                            if (createPattern != null) {
                                createPattern.close();
                                return;
                            }
                            return;
                        } catch (Throwable th) {
                            th.addSuppressed(th);
                        }
                    } else {
                        throw new IllegalStateException("null chosen pattern in stage 'need to confirm");
                    }
                } else if (PrivacySpaceChooseLockPatternFragment.this.mUiStage != Stage.Introduction && PrivacySpaceChooseLockPatternFragment.this.mUiStage != Stage.ChoiceTooShort && PrivacySpaceChooseLockPatternFragment.this.mUiStage != Stage.ChoiceSameAsMainSpace) {
                    throw new IllegalStateException("Unexpected stage " + PrivacySpaceChooseLockPatternFragment.this.mUiStage + " when entering the pattern.");
                } else if (list.size() < 4) {
                    PrivacySpaceChooseLockPatternFragment.this.updateStage(Stage.ChoiceTooShort);
                    return;
                } else {
                    PrivacySpaceChooseLockPatternFragment.this.mChosenPattern = LockscreenCredential.createPattern(list);
                    PrivacySpaceChooseLockPatternFragment privacySpaceChooseLockPatternFragment = PrivacySpaceChooseLockPatternFragment.this;
                    privacySpaceChooseLockPatternFragment.startCheckPassword(privacySpaceChooseLockPatternFragment.mChosenPattern);
                    return;
                }
                throw th;
            }

            private void patternInProgress() {
                PrivacySpaceChooseLockPatternFragment.this.mHeaderText.setText(C1992R$string.lockpattern_recording_inprogress);
                if (PrivacySpaceChooseLockPatternFragment.this.mDefaultHeaderColorList != null) {
                    PrivacySpaceChooseLockPatternFragment privacySpaceChooseLockPatternFragment = PrivacySpaceChooseLockPatternFragment.this;
                    privacySpaceChooseLockPatternFragment.mHeaderText.setTextColor(privacySpaceChooseLockPatternFragment.mDefaultHeaderColorList);
                }
                PrivacySpaceChooseLockPatternFragment.this.mFooterText.setText("");
                PrivacySpaceChooseLockPatternFragment.this.mNextButton.setEnabled(false);
            }
        };
        protected LockscreenCredential mChosenPattern;
        /* access modifiers changed from: private */
        public Runnable mClearPatternRunnable = new Runnable() {
            public void run() {
                PrivacySpaceChooseLockPatternFragment.this.mLockPatternView.clearPattern();
            }
        };
        /* access modifiers changed from: private */
        public ColorStateList mDefaultHeaderColorList;
        protected TextView mFooterText;
        protected boolean mForFingerprint;
        protected TextView mHeaderText;
        private boolean mIsCreatePrivacySpace = true;
        protected boolean mIsManagedProfile;
        private LockPatternUtils mLockPatternUtils;
        protected LockPatternView mLockPatternView;
        private LockscreenCredential mMainChosenPattern;
        private LockscreenCredential mMainUserPattern;
        /* access modifiers changed from: private */
        public FooterButton mNextButton;
        private int mPrivacyUserId;
        private LockscreenCredential mPrivacyUserPattern;
        private boolean mRequestGatekeeperPassword;
        private SaveAndFinishWorker mSaveAndFinishWorker;
        protected FooterButton mSkipOrClearButton;
        /* access modifiers changed from: private */
        public Stage mUiStage = Stage.Introduction;
        protected int mUserId;
        UserManager mUserManager;

        public int getMetricsCategory() {
            return 29;
        }

        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i == 105) {
                if (intent == null) {
                    intent = new Intent();
                }
                intent.putExtra("privacy_space_password", this.mChosenPattern);
                getActivity().setResult(i2, intent);
                getActivity().finish();
            }
        }

        /* access modifiers changed from: protected */
        public void setRightButtonEnabled(boolean z) {
            this.mNextButton.setEnabled(z);
        }

        /* access modifiers changed from: protected */
        public void setRightButtonText(int i) {
            this.mNextButton.setText(getActivity(), i);
        }

        enum LeftButtonMode {
            Retry(r1, true),
            RetryDisabled(r1, false),
            Gone(-1, false);
            
            final boolean enabled;
            final int text;

            private LeftButtonMode(int i, boolean z) {
                this.text = i;
                this.enabled = z;
            }
        }

        enum RightButtonMode {
            Continue(r1, true),
            ContinueDisabled(r1, false),
            Confirm(r5, true),
            ConfirmDisabled(r5, false),
            Ok(17039370, true);
            
            final boolean enabled;
            final int text;

            private RightButtonMode(int i, boolean z) {
                this.text = i;
                this.enabled = z;
            }
        }

        protected enum Stage {
            Introduction(r3, r4, r5, r23, r7, -1, true),
            HelpScreen(-1, -1, C1992R$string.lockpattern_settings_help_how_to_record, r23, RightButtonMode.Ok, -1, false),
            ChoiceTooShort(r3, r4, r5, r6, r7, -1, true),
            ChoiceSameAsMainSpace(r17, r17, C1992R$string.lockpassword_privacy_pattern_same_as_main_space, r27, r24, -1, true),
            FirstChoiceValid(r3, r4, C1992R$string.lockpattern_pattern_entered_header, r6, RightButtonMode.Continue, -1, false),
            NeedToConfirm(-1, -1, r18, r19, r20, -1, true),
            ConfirmWrong(-1, -1, C1992R$string.lockpattern_need_to_unlock_wrong, r19, r20, -1, true),
            ChoiceConfirmed(-1, -1, C1992R$string.lockpattern_pattern_confirmed_header, r19, RightButtonMode.Confirm, -1, false);
            
            final int footerMessage;
            final int headerMessage;
            final LeftButtonMode leftMode;
            final int message;
            final int messageForBiometrics;
            final boolean patternEnabled;
            final RightButtonMode rightMode;

            private Stage(int i, int i2, int i3, LeftButtonMode leftButtonMode, RightButtonMode rightButtonMode, int i4, boolean z) {
                this.headerMessage = i3;
                this.messageForBiometrics = i;
                this.message = i2;
                this.leftMode = leftButtonMode;
                this.rightMode = rightButtonMode;
                this.footerMessage = i4;
                this.patternEnabled = z;
            }
        }

        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            if (getActivity() instanceof PrivacySpaceChooseLockPattern) {
                Intent intent = getActivity().getIntent();
                this.mUserId = Utils.getUserIdFromBundle(getActivity(), intent.getExtras());
                this.mIsManagedProfile = UserManager.get(getActivity()).isManagedProfile(this.mUserId);
                this.mLockPatternUtils = new LockPatternUtils(getActivity());
                this.mIsCreatePrivacySpace = intent.getBooleanExtra("setting_from_privacy_space", false);
                UserManager userManager = (UserManager) getActivity().getSystemService("user");
                this.mUserManager = userManager;
                this.mPrivacyUserId = userManager.getPrivacySpaceUserId();
                this.mMainUserPattern = intent.getParcelableExtra("password");
                this.mMainChosenPattern = intent.getParcelableExtra("main_space_password");
                this.mPrivacyUserPattern = intent.getParcelableExtra("privacy_space_current_password");
                this.mForFingerprint = intent.getBooleanExtra("for_fingerprint", false);
                return;
            }
            throw new SecurityException("Fragment contained in wrong activity");
        }

        private void updateActivityTitle() {
            int i;
            if (this.mForFingerprint) {
                i = C1992R$string.lockpassword_choose_your_pattern_header_for_fingerprint;
            } else {
                i = C1992R$string.lockpassword_choose_your_privacy_space_pattern_header;
            }
            getActivity().setTitle(i);
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            GlifLayout glifLayout = (GlifLayout) layoutInflater.inflate(C1987R$layout.choose_lock_pattern, viewGroup, false);
            updateActivityTitle();
            glifLayout.setHeaderText(getActivity().getTitle());
            glifLayout.getHeaderTextView().setAccessibilityLiveRegion(1);
            if (getResources().getBoolean(C1980R$bool.config_lock_pattern_minimal_ui)) {
                if (glifLayout.findViewById(C1985R$id.sud_layout_icon) != null) {
                    ((IconMixin) glifLayout.getMixin(IconMixin.class)).setVisibility(8);
                }
            } else if (this.mForFingerprint) {
                glifLayout.setIcon(getActivity().getDrawable(C1983R$drawable.ic_fingerprint_header));
            }
            FooterBarMixin footerBarMixin = (FooterBarMixin) glifLayout.getMixin(FooterBarMixin.class);
            footerBarMixin.setSecondaryButton(new FooterButton.Builder(getActivity()).setText(C1992R$string.lockpattern_tutorial_cancel_label).setListener(new C1962xb399277c(this)).setButtonType(0).setTheme(C1993R$style.SudGlifButton_Secondary).build());
            footerBarMixin.setPrimaryButton(new FooterButton.Builder(getActivity()).setText(C1992R$string.lockpattern_tutorial_continue_label).setListener(new C1961xb399277b(this)).setButtonType(5).setTheme(C1993R$style.SudGlifButton_Primary).build());
            this.mSkipOrClearButton = footerBarMixin.getSecondaryButton();
            this.mNextButton = footerBarMixin.getPrimaryButton();
            return glifLayout;
        }

        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            TextView textView = (TextView) view.findViewById(C1985R$id.headerText);
            this.mHeaderText = textView;
            this.mDefaultHeaderColorList = textView.getTextColors();
            LockPatternView findViewById = view.findViewById(C1985R$id.lockPattern);
            this.mLockPatternView = findViewById;
            findViewById.setOnPatternListener(this.mChooseNewLockPatternListener);
            this.mLockPatternView.setTactileFeedbackEnabled(this.mLockPatternUtils.isTactileFeedbackEnabled());
            this.mLockPatternView.setFadePattern(false);
            this.mFooterText = (TextView) view.findViewById(C1985R$id.footerText);
            view.findViewById(C1985R$id.topLayout).setDefaultTouchRecepient(this.mLockPatternView);
            boolean booleanExtra = getActivity().getIntent().getBooleanExtra("confirm_credentials", true);
            this.mRequestGatekeeperPassword = getActivity().getIntent().getBooleanExtra("request_gk_pw_handle", false);
            if (bundle != null) {
                this.mSaveAndFinishWorker = (SaveAndFinishWorker) getFragmentManager().findFragmentByTag("save_and_finish_worker");
                this.mChosenPattern = bundle.getParcelable("chosenPattern");
                updateStage(Stage.values()[bundle.getInt("uiStage")]);
            } else if (booleanExtra) {
                updateStage(Stage.NeedToConfirm);
                if (!new ChooseLockSettingsHelper.Builder(getActivity()).setRequestCode(55).setTitle(getString(C1992R$string.unlock_set_unlock_launch_picker_title)).setReturnCredentials(true).setRequestGatekeeperPasswordHandle(this.mRequestGatekeeperPassword).setUserId(this.mUserId).show()) {
                    updateStage(Stage.Introduction);
                }
            } else {
                updateStage(Stage.Introduction);
            }
        }

        public void onResume() {
            super.onResume();
            updateStage(this.mUiStage);
            SaveAndFinishWorker saveAndFinishWorker = this.mSaveAndFinishWorker;
            if (saveAndFinishWorker != null) {
                saveAndFinishWorker.setListener(this);
            }
        }

        public void onPause() {
            SaveAndFinishWorker saveAndFinishWorker = this.mSaveAndFinishWorker;
            if (saveAndFinishWorker != null) {
                saveAndFinishWorker.setListener((SaveChosenLockWorkerBase.Listener) null);
            }
            super.onPause();
        }

        public void onDestroy() {
            super.onDestroy();
            System.gc();
            System.runFinalization();
            System.gc();
        }

        /* access modifiers changed from: protected */
        public Intent getRedactionInterstitialIntent(Context context) {
            return PrivacySpaceRedactionInterstitial.createStartIntent(context);
        }

        public void handleLeftButton() {
            if (this.mUiStage.leftMode == LeftButtonMode.Retry) {
                LockscreenCredential lockscreenCredential = this.mChosenPattern;
                if (lockscreenCredential != null) {
                    lockscreenCredential.zeroize();
                    this.mChosenPattern = null;
                }
                this.mLockPatternView.clearPattern();
                updateStage(Stage.Introduction);
                return;
            }
            throw new IllegalStateException("left footer button pressed, but stage of " + this.mUiStage + " doesn't make sense");
        }

        public void handleRightButton() {
            Stage stage = this.mUiStage;
            RightButtonMode rightButtonMode = stage.rightMode;
            RightButtonMode rightButtonMode2 = RightButtonMode.Continue;
            if (rightButtonMode == rightButtonMode2) {
                Stage stage2 = Stage.FirstChoiceValid;
                if (stage == stage2) {
                    updateStage(Stage.NeedToConfirm);
                    return;
                }
                throw new IllegalStateException("expected ui stage " + stage2 + " when button is " + rightButtonMode2);
            }
            RightButtonMode rightButtonMode3 = RightButtonMode.Confirm;
            if (rightButtonMode == rightButtonMode3) {
                Stage stage3 = Stage.ChoiceConfirmed;
                if (stage != stage3) {
                    throw new IllegalStateException("expected ui stage " + stage3 + " when button is " + rightButtonMode3);
                } else if (this.mIsCreatePrivacySpace) {
                    startSaveAndFinish();
                } else {
                    startSaveAndFinishModify();
                }
            } else if (rightButtonMode != RightButtonMode.Ok) {
            } else {
                if (stage == Stage.HelpScreen) {
                    this.mLockPatternView.clearPattern();
                    this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
                    updateStage(Stage.Introduction);
                    return;
                }
                throw new IllegalStateException("Help screen is only mode with ok button, but stage is " + this.mUiStage);
            }
        }

        /* access modifiers changed from: protected */
        public void onSkipOrClearButtonClick(View view) {
            handleLeftButton();
        }

        /* access modifiers changed from: protected */
        public void onNextButtonClick(View view) {
            handleRightButton();
        }

        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putInt("uiStage", this.mUiStage.ordinal());
            LockscreenCredential lockscreenCredential = this.mChosenPattern;
            if (lockscreenCredential != null) {
                bundle.putParcelable("chosenPattern", lockscreenCredential);
            }
        }

        /* access modifiers changed from: protected */
        public void updateStage(Stage stage) {
            Stage stage2 = this.mUiStage;
            this.mUiStage = stage;
            Stage stage3 = Stage.ChoiceTooShort;
            boolean z = false;
            if (stage == stage3) {
                this.mHeaderText.setText(getResources().getString(stage.headerMessage, new Object[]{4}));
            } else {
                this.mHeaderText.setText(stage.headerMessage);
            }
            GlifLayout glifLayout = (GlifLayout) getActivity().findViewById(C1985R$id.setup_wizard_layout);
            boolean z2 = this.mForFingerprint;
            int i = z2 ? stage.messageForBiometrics : stage.message;
            if (i == -1) {
                glifLayout.setDescriptionText((CharSequence) "");
            } else {
                glifLayout.setDescriptionText(i);
            }
            int i2 = stage.footerMessage;
            if (i2 == -1) {
                this.mFooterText.setText("");
            } else {
                this.mFooterText.setText(i2);
            }
            if (stage == Stage.ConfirmWrong || stage == stage3 || stage == Stage.ChoiceSameAsMainSpace) {
                TypedValue typedValue = new TypedValue();
                getActivity().getTheme().resolveAttribute(C1979R$attr.colorError, typedValue, true);
                this.mHeaderText.setTextColor(typedValue.data);
            } else {
                ColorStateList colorStateList = this.mDefaultHeaderColorList;
                if (colorStateList != null) {
                    this.mHeaderText.setTextColor(colorStateList);
                }
                if (stage == Stage.NeedToConfirm && z2) {
                    this.mHeaderText.setText("");
                    glifLayout.setHeaderText(C1992R$string.lockpassword_draw_your_pattern_again_header);
                }
            }
            updateFooterLeftButton(stage);
            setRightButtonText(stage.rightMode.text);
            setRightButtonEnabled(stage.rightMode.enabled);
            if (stage.patternEnabled) {
                this.mLockPatternView.enableInput();
            } else {
                this.mLockPatternView.disableInput();
            }
            this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);
            switch (C19581.f170x7d0afbee[this.mUiStage.ordinal()]) {
                case 1:
                case 2:
                    this.mLockPatternView.clearPattern();
                    break;
                case 3:
                    this.mLockPatternView.setPattern(LockPatternView.DisplayMode.Animate, this.mAnimatePattern);
                    break;
                case 4:
                case 5:
                case 6:
                    this.mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    postClearPatternRunnable();
                    z = true;
                    break;
            }
            if (stage2 != stage || z) {
                TextView textView = this.mHeaderText;
                textView.announceForAccessibility(textView.getText());
            }
        }

        /* access modifiers changed from: protected */
        public void updateFooterLeftButton(Stage stage) {
            if (stage.leftMode == LeftButtonMode.Gone) {
                this.mSkipOrClearButton.setVisibility(8);
                return;
            }
            this.mSkipOrClearButton.setVisibility(0);
            this.mSkipOrClearButton.setText(getActivity(), stage.leftMode.text);
            this.mSkipOrClearButton.setEnabled(stage.leftMode.enabled);
        }

        private void postClearPatternRunnable() {
            this.mLockPatternView.removeCallbacks(this.mClearPatternRunnable);
            this.mLockPatternView.postDelayed(this.mClearPatternRunnable, 2000);
        }

        /* access modifiers changed from: private */
        public void startCheckPassword(LockscreenCredential lockscreenCredential) {
            int credentialOwnerProfile = UserManager.get(getActivity()).getCredentialOwnerProfile(this.mUserId);
            LockPatternUtils lockPatternUtils = new LockPatternUtils(getActivity());
            this.mLockPatternUtils = lockPatternUtils;
            LockPatternChecker.checkCredential(lockPatternUtils, lockscreenCredential, credentialOwnerProfile, new C1963xb399277d(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$startCheckPassword$0(boolean z, int i) {
            onPasswordChecked(z);
        }

        private void onPasswordChecked(boolean z) {
            LockscreenCredential lockscreenCredential = this.mMainChosenPattern;
            boolean z2 = lockscreenCredential != null && lockscreenCredential.equals(this.mChosenPattern);
            if (z || z2) {
                updateStage(Stage.ChoiceSameAsMainSpace);
            } else {
                updateStage(Stage.FirstChoiceValid);
            }
        }

        private void startSaveAndFinish() {
            Log.i("PrivacySpaceChooseLockPattern", "setPassword success");
            startActivityForResult(getRedactionInterstitialIntent(getActivity()), R$styleable.Constraint_pathMotionArc);
        }

        public void onChosenLockSaveFinished(boolean z, Intent intent) {
            getActivity().setResult(1, intent);
            LockscreenCredential lockscreenCredential = this.mChosenPattern;
            if (lockscreenCredential != null) {
                lockscreenCredential.zeroize();
            }
            LockscreenCredential lockscreenCredential2 = this.mMainChosenPattern;
            if (lockscreenCredential2 != null) {
                lockscreenCredential2.zeroize();
            }
            LockscreenCredential lockscreenCredential3 = this.mMainUserPattern;
            if (lockscreenCredential3 != null) {
                lockscreenCredential3.zeroize();
            }
            LockscreenCredential lockscreenCredential4 = this.mPrivacyUserPattern;
            if (lockscreenCredential4 != null) {
                lockscreenCredential4.zeroize();
            }
            getActivity().finish();
        }

        private void startSaveAndFinishModify() {
            if (this.mSaveAndFinishWorker != null) {
                Log.w("PrivacySpaceChooseLockPattern", "startSaveAndFinish with an existing SaveAndFinishWorker.");
                return;
            }
            setRightButtonEnabled(false);
            SaveAndFinishWorker saveAndFinishWorker = new SaveAndFinishWorker();
            this.mSaveAndFinishWorker = saveAndFinishWorker;
            saveAndFinishWorker.setListener(this);
            getFragmentManager().beginTransaction().add((Fragment) this.mSaveAndFinishWorker, "save_and_finish_worker").commit();
            getFragmentManager().executePendingTransactions();
            this.mSaveAndFinishWorker.start(this.mLockPatternUtils, this.mMainChosenPattern, this.mMainUserPattern, this.mChosenPattern, this.mPrivacyUserPattern, this.mUserId, this.mPrivacyUserId, false);
        }
    }

    /* renamed from: com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern$1 */
    static /* synthetic */ class C19581 {

        /* renamed from: $SwitchMap$com$motorola$settings$security$screenlock$PrivacySpaceChooseLockPattern$PrivacySpaceChooseLockPatternFragment$Stage */
        static final /* synthetic */ int[] f170x7d0afbee;

        /* JADX WARNING: Can't wrap try/catch for region: R(18:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|18) */
        /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x0054 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern$PrivacySpaceChooseLockPatternFragment$Stage[] r0 = com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern.PrivacySpaceChooseLockPatternFragment.Stage.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f170x7d0afbee = r0
                com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern$PrivacySpaceChooseLockPatternFragment$Stage r1 = com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern.PrivacySpaceChooseLockPatternFragment.Stage.Introduction     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = f170x7d0afbee     // Catch:{ NoSuchFieldError -> 0x001d }
                com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern$PrivacySpaceChooseLockPatternFragment$Stage r1 = com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern.PrivacySpaceChooseLockPatternFragment.Stage.NeedToConfirm     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = f170x7d0afbee     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern$PrivacySpaceChooseLockPatternFragment$Stage r1 = com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern.PrivacySpaceChooseLockPatternFragment.Stage.HelpScreen     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = f170x7d0afbee     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern$PrivacySpaceChooseLockPatternFragment$Stage r1 = com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern.PrivacySpaceChooseLockPatternFragment.Stage.ChoiceTooShort     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = f170x7d0afbee     // Catch:{ NoSuchFieldError -> 0x003e }
                com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern$PrivacySpaceChooseLockPatternFragment$Stage r1 = com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern.PrivacySpaceChooseLockPatternFragment.Stage.ChoiceSameAsMainSpace     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = f170x7d0afbee     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern$PrivacySpaceChooseLockPatternFragment$Stage r1 = com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern.PrivacySpaceChooseLockPatternFragment.Stage.ConfirmWrong     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = f170x7d0afbee     // Catch:{ NoSuchFieldError -> 0x0054 }
                com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern$PrivacySpaceChooseLockPatternFragment$Stage r1 = com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern.PrivacySpaceChooseLockPatternFragment.Stage.FirstChoiceValid     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                int[] r0 = f170x7d0afbee     // Catch:{ NoSuchFieldError -> 0x0060 }
                com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern$PrivacySpaceChooseLockPatternFragment$Stage r1 = com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern.PrivacySpaceChooseLockPatternFragment.Stage.ChoiceConfirmed     // Catch:{ NoSuchFieldError -> 0x0060 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0060 }
                r2 = 8
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0060 }
            L_0x0060:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.motorola.settings.security.screenlock.PrivacySpaceChooseLockPattern.C19581.<clinit>():void");
        }
    }

    public static class SaveAndFinishWorker extends SaveChosenLockWorkerBase {
        private boolean isSixPinLock;
        private boolean mLockVirgin;
        private LockscreenCredential mMainChosenPattern;
        private LockscreenCredential mMainCurrentCredential;
        private LockscreenCredential mPrivacyChosenPassword;
        private LockscreenCredential mPrivacyCurrentCredential;
        private boolean mPrivacyLockVirgin;
        private int mPrivacyUserId;

        public void start(LockPatternUtils lockPatternUtils, LockscreenCredential lockscreenCredential, LockscreenCredential lockscreenCredential2, LockscreenCredential lockscreenCredential3, LockscreenCredential lockscreenCredential4, int i, int i2, boolean z) {
            prepare(lockPatternUtils, false, false, i);
            if (lockscreenCredential == null) {
                lockscreenCredential = LockscreenCredential.createNone();
            }
            this.mMainChosenPattern = lockscreenCredential;
            if (lockscreenCredential2 == null) {
                lockscreenCredential2 = LockscreenCredential.createNone();
            }
            this.mMainCurrentCredential = lockscreenCredential2;
            this.mPrivacyChosenPassword = lockscreenCredential3;
            if (lockscreenCredential4 == null) {
                lockscreenCredential4 = LockscreenCredential.createNone();
            }
            this.mPrivacyCurrentCredential = lockscreenCredential4;
            this.mPrivacyUserId = i2;
            this.mUserId = i;
            this.mLockVirgin = !this.mUtils.isPatternEverChosen(i);
            this.mPrivacyLockVirgin = !this.mUtils.isPatternEverChosen(this.mPrivacyUserId);
            this.isSixPinLock = z;
            start();
        }

        /* access modifiers changed from: protected */
        public Pair<Boolean, Intent> saveAndVerifyInBackground() {
            Log.i("PrivacySpaceChooseLockPattern", "save password,mUserId:" + this.mUserId + ",mPrivacyUserId:" + this.mPrivacyUserId);
            boolean lockCredential = this.mUtils.setLockCredential(this.mMainChosenPattern, this.mMainCurrentCredential, this.mUserId);
            boolean lockCredential2 = this.mUtils.setLockCredential(this.mPrivacyChosenPassword, this.mPrivacyCurrentCredential, this.mPrivacyUserId);
            Log.i("PrivacySpaceChooseLockPattern", "save password,mainSuccess:" + lockCredential + ",privacySuccess:" + lockCredential2);
            boolean z = lockCredential && lockCredential2;
            if (z) {
                this.mUtils.setSixBitForPin(this.isSixPinLock, this.mUserId);
                this.mUtils.setSixBitForPin(this.isSixPinLock, this.mPrivacyUserId);
            }
            return Pair.create(Boolean.valueOf(z), (Object) null);
        }

        /* access modifiers changed from: protected */
        public void finish(Intent intent) {
            if (this.mLockVirgin) {
                this.mUtils.setVisiblePatternEnabled(true, this.mUserId);
            }
            if (this.mPrivacyLockVirgin) {
                this.mUtils.setVisiblePatternEnabled(true, this.mPrivacyUserId);
            }
            super.finish(intent);
        }
    }
}

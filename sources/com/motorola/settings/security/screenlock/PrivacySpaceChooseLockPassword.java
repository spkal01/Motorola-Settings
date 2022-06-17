package com.motorola.settings.security.screenlock;

import android.app.admin.PasswordMetrics;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Insets;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImeAwareEditText;
import android.widget.TextView;
import androidx.constraintlayout.widget.R$styleable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockscreenCredential;
import com.android.internal.widget.PasswordValidationError;
import com.android.internal.widget.TextViewInputDisabler;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1990R$plurals;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import com.android.settings.SettingsActivity;
import com.android.settings.SetupWizardUtils;
import com.android.settings.Utils;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.password.PasswordRequirementAdapter;
import com.android.settings.password.SaveChosenLockWorkerBase;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.util.ThemeHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrivacySpaceChooseLockPassword extends SettingsSetUpActivity {
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
            Intent intent = new Intent(context, PrivacySpaceChooseLockPassword.class);
            this.mIntent = intent;
            intent.putExtra("confirm_credentials", false);
            intent.putExtra("extra_require_password", false);
        }

        public IntentBuilder setPasswordType(int i) {
            this.mIntent.putExtra("lockscreen.password_type", i);
            return this;
        }

        public IntentBuilder setPasswordRequirement(int i, PasswordMetrics passwordMetrics) {
            this.mIntent.putExtra("min_complexity", i);
            this.mIntent.putExtra("min_metrics", passwordMetrics);
            return this;
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

        public IntentBuilder setMainUserIsSixPin(boolean z) {
            this.mIntent.putExtra("main_space_is_six_pin", z);
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
        return PrivacySpaceChooseLockPasswordFragment.class.getName().equals(str);
    }

    /* access modifiers changed from: package-private */
    public Class<? extends Fragment> getFragmentClass() {
        return PrivacySpaceChooseLockPasswordFragment.class;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        ThemeHelper.trySetDynamicColor(this);
        super.onCreate(bundle);
        findViewById(C1985R$id.content_parent).setFitsSystemWindows(false);
    }

    public static class PrivacySpaceChooseLockPasswordFragment extends InstrumentedFragment implements TextView.OnEditorActionListener, TextWatcher, SaveChosenLockWorkerBase.Listener {
        private LockscreenCredential mChosenPassword;
        private TextViewInputDisabler mCurrentEntryInputDisabler;
        private ImeAwareEditText mCurrentPasswordEntry;
        private LockscreenCredential mFirstPassword;
        protected boolean mForFingerprint;
        protected boolean mIsAlphaMode;
        private boolean mIsCreatePrivacySpace = true;
        private boolean mIsMainUserChangeToSixPin = false;
        private GlifLayout mLayout;
        private LockPatternUtils mLockPatternUtils;
        private LockscreenCredential mMainChosenPassword;
        private LockscreenCredential mMainUserPassword;
        private TextView mMessage;
        private int mMinComplexity = 0;
        private PasswordMetrics mMinMetrics;
        private FooterButton mNextButton;
        private byte[] mPasswordHistoryHashFactor;
        private PasswordRequirementAdapter mPasswordRequirementAdapter;
        private RecyclerView mPasswordRestrictionView;
        private int mPasswordType = 131072;
        private boolean mPinForSixBit = false;
        private int mPrivacyUserId;
        private LockscreenCredential mPrivacyUserPassword;
        private SaveAndFinishWorker mSaveAndFinishWorker;
        protected FooterButton mSkipOrClearButton;
        private TextChangedHandler mTextChangedHandler;
        protected Stage mUiStage = Stage.Introduction;
        protected int mUserId;
        UserManager mUserManager;
        private List<PasswordValidationError> mValidationErrors;

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public int getMetricsCategory() {
            return 28;
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        /* access modifiers changed from: protected */
        public int toVisibility(boolean z) {
            return z ? 0 : 8;
        }

        protected enum Stage {
            Introduction(r9, r4, r5, r6, r7, r8, r9, r12, C1992R$string.lockpassword_choose_pin_description, r12, C1992R$string.next_label),
            NeedToConfirm(r20, r20, r20, r23, r23, r23, 0, 0, 0, 0, r37),
            ConfirmWrong(r29, r29, r29, r32, r32, r32, 0, 0, 0, 0, r37);
            
            public final int alphaHint;
            public final int alphaHintForFingerprint;
            public final int alphaHintForPrivacySpace;
            public final int alphaMessage;
            public final int alphaMessageForBiometrics;
            public final int buttonText;
            public final int numericHint;
            public final int numericHintForFingerprint;
            public final int numericHintForPrivacySpace;
            public final int numericMessage;
            public final int numericMessageForBiometrics;

            private Stage(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11) {
                this.alphaHint = i;
                this.alphaHintForFingerprint = i2;
                this.alphaHintForPrivacySpace = i3;
                this.numericHint = i4;
                this.numericHintForFingerprint = i5;
                this.numericHintForPrivacySpace = i6;
                this.alphaMessage = i7;
                this.alphaMessageForBiometrics = i8;
                this.numericMessage = i9;
                this.numericMessageForBiometrics = i10;
                this.buttonText = i11;
            }

            public int getHint(boolean z, int i) {
                if (z) {
                    if (i == 1) {
                        return this.alphaHintForFingerprint;
                    }
                    return this.alphaHintForPrivacySpace;
                } else if (i == 1) {
                    return this.numericHintForFingerprint;
                } else {
                    return this.numericHintForPrivacySpace;
                }
            }

            public int getMessage(boolean z, int i) {
                return i != 1 ? z ? this.alphaMessage : this.numericMessage : z ? this.alphaMessageForBiometrics : this.numericMessageForBiometrics;
            }
        }

        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mLockPatternUtils = new LockPatternUtils(getActivity());
            Intent intent = getActivity().getIntent();
            if (getActivity() instanceof PrivacySpaceChooseLockPassword) {
                this.mUserId = Utils.getUserIdFromBundle(getActivity(), intent.getExtras());
                this.mForFingerprint = intent.getBooleanExtra("for_fingerprint", false);
                this.mPasswordType = intent.getIntExtra("lockscreen.password_type", 131072);
                this.mMinComplexity = intent.getIntExtra("min_complexity", 0);
                PasswordMetrics parcelableExtra = intent.getParcelableExtra("min_metrics");
                this.mMinMetrics = parcelableExtra;
                if (parcelableExtra == null) {
                    this.mMinMetrics = new PasswordMetrics(-1);
                }
                UserManager userManager = (UserManager) getActivity().getSystemService("user");
                this.mUserManager = userManager;
                this.mPrivacyUserId = userManager.getPrivacySpaceUserId();
                this.mIsMainUserChangeToSixPin = intent.getBooleanExtra("main_space_is_six_pin", false);
                this.mIsCreatePrivacySpace = intent.getBooleanExtra("setting_from_privacy_space", false);
                this.mMainUserPassword = intent.getParcelableExtra("password");
                this.mMainChosenPassword = intent.getParcelableExtra("main_space_password");
                this.mPrivacyUserPassword = intent.getParcelableExtra("privacy_space_current_password");
                if (this.mIsMainUserChangeToSixPin) {
                    this.mPinForSixBit = true;
                } else if (this.mIsCreatePrivacySpace) {
                    this.mPinForSixBit = this.mLockPatternUtils.isSixBitForPinLock(this.mUserId);
                }
                this.mTextChangedHandler = new TextChangedHandler();
                return;
            }
            throw new SecurityException("Fragment contained in wrong activity");
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            return layoutInflater.inflate(C1987R$layout.choose_lock_password_privacy_space, viewGroup, false);
        }

        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            this.mLayout = (GlifLayout) view;
            ((ViewGroup) view.findViewById(C1985R$id.password_container)).setOpticalInsets(Insets.NONE);
            FooterBarMixin footerBarMixin = (FooterBarMixin) this.mLayout.getMixin(FooterBarMixin.class);
            footerBarMixin.setSecondaryButton(new FooterButton.Builder(getActivity()).setText(C1992R$string.lockpassword_clear_label).setListener(new C1956x55c90404(this)).setButtonType(7).setTheme(C1993R$style.SudGlifButton_Secondary).build());
            footerBarMixin.setPrimaryButton(new FooterButton.Builder(getActivity()).setText(C1992R$string.next_label).setListener(new C1955x55c90403(this)).setButtonType(5).setTheme(C1993R$style.SudGlifButton_Primary).build());
            this.mSkipOrClearButton = footerBarMixin.getSecondaryButton();
            this.mNextButton = footerBarMixin.getPrimaryButton();
            this.mMessage = (TextView) view.findViewById(C1985R$id.sud_layout_description);
            if (this.mForFingerprint) {
                this.mLayout.setIcon(getActivity().getDrawable(C1983R$drawable.ic_fingerprint_header));
            }
            int i = this.mPasswordType;
            this.mIsAlphaMode = 262144 == i || 327680 == i || 393216 == i;
            setupPasswordRequirementsView(view);
            this.mPasswordRestrictionView.setLayoutManager(new LinearLayoutManager(getActivity()));
            ImeAwareEditText findViewById = view.findViewById(C1985R$id.password_entry);
            initPasswordEntry(findViewById);
            TextViewInputDisabler textViewInputDisabler = new TextViewInputDisabler(findViewById);
            ImeAwareEditText findViewById2 = view.findViewById(C1985R$id.password_entry_six_bit);
            initPasswordEntry(findViewById2);
            TextViewInputDisabler textViewInputDisabler2 = new TextViewInputDisabler(findViewById2);
            if (this.mIsAlphaMode || !this.mPinForSixBit) {
                this.mMinMetrics.length = 4;
                findViewById2.setVisibility(8);
                findViewById.setVisibility(0);
                this.mCurrentPasswordEntry = findViewById;
                this.mCurrentEntryInputDisabler = textViewInputDisabler;
            } else {
                this.mMinMetrics.length = 6;
                findViewById2.setVisibility(0);
                findViewById.setVisibility(8);
                this.mCurrentPasswordEntry = findViewById2;
                this.mCurrentEntryInputDisabler = textViewInputDisabler2;
            }
            FragmentActivity activity = getActivity();
            if (bundle == null) {
                updateStage(Stage.Introduction);
            } else {
                this.mFirstPassword = bundle.getParcelable("first_password");
                this.mSaveAndFinishWorker = (SaveAndFinishWorker) getFragmentManager().findFragmentByTag("save_and_finish_worker");
                String string = bundle.getString("ui_stage");
                if (string != null) {
                    Stage valueOf = Stage.valueOf(string);
                    this.mUiStage = valueOf;
                    updateStage(valueOf);
                }
            }
            if (activity instanceof SettingsActivity) {
                int hint = Stage.Introduction.getHint(this.mIsAlphaMode, getStageType());
                ((SettingsActivity) activity).setTitle(hint);
                this.mLayout.setHeaderText(hint);
            }
        }

        private void initPasswordEntry(ImeAwareEditText imeAwareEditText) {
            imeAwareEditText.setOnEditorActionListener(this);
            imeAwareEditText.addTextChangedListener(this);
            int inputType = imeAwareEditText.getInputType();
            if (!this.mIsAlphaMode) {
                inputType = 18;
            }
            imeAwareEditText.setInputType(inputType);
            if (this.mIsAlphaMode) {
                imeAwareEditText.setContentDescription(getString(C1992R$string.unlock_set_unlock_password_title));
            } else {
                imeAwareEditText.setContentDescription(getString(C1992R$string.unlock_set_unlock_pin_title));
            }
            imeAwareEditText.setTypeface(Typeface.create(getContext().getString(17039962), 0));
        }

        public void onDestroy() {
            super.onDestroy();
            LockscreenCredential lockscreenCredential = this.mChosenPassword;
            if (lockscreenCredential != null) {
                lockscreenCredential.zeroize();
            }
            System.gc();
            System.runFinalization();
            System.gc();
        }

        /* access modifiers changed from: protected */
        public int getStageType() {
            return this.mForFingerprint ? 1 : 0;
        }

        private void setupPasswordRequirementsView(View view) {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(C1985R$id.password_requirements_view);
            this.mPasswordRestrictionView = recyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            PasswordRequirementAdapter passwordRequirementAdapter = new PasswordRequirementAdapter();
            this.mPasswordRequirementAdapter = passwordRequirementAdapter;
            this.mPasswordRestrictionView.setAdapter(passwordRequirementAdapter);
        }

        public void onResume() {
            super.onResume();
            updateStage(this.mUiStage);
            SaveAndFinishWorker saveAndFinishWorker = this.mSaveAndFinishWorker;
            if (saveAndFinishWorker != null) {
                saveAndFinishWorker.setListener(this);
                return;
            }
            this.mCurrentPasswordEntry.requestFocus();
            this.mCurrentPasswordEntry.scheduleShowSoftInput();
        }

        public void onPause() {
            SaveAndFinishWorker saveAndFinishWorker = this.mSaveAndFinishWorker;
            if (saveAndFinishWorker != null) {
                saveAndFinishWorker.setListener((SaveChosenLockWorkerBase.Listener) null);
            }
            super.onPause();
        }

        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putString("ui_stage", this.mUiStage.name());
            bundle.putParcelable("first_password", this.mFirstPassword);
        }

        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i == 105) {
                if (intent == null) {
                    intent = new Intent();
                }
                intent.putExtra("privacy_space_password", this.mChosenPassword);
                getActivity().setResult(i2, intent);
                getActivity().finish();
            }
        }

        /* access modifiers changed from: protected */
        public Intent getRedactionInterstitialIntent(Context context) {
            return PrivacySpaceRedactionInterstitial.createStartIntent(context);
        }

        /* access modifiers changed from: protected */
        public void updateStage(Stage stage) {
            Stage stage2 = this.mUiStage;
            this.mUiStage = stage;
            updateUi();
            if (stage2 != stage) {
                GlifLayout glifLayout = this.mLayout;
                glifLayout.announceForAccessibility(glifLayout.getHeaderText());
            }
        }

        /* access modifiers changed from: package-private */
        @VisibleForTesting
        public boolean validatePassword(LockscreenCredential lockscreenCredential) {
            byte[] credential = lockscreenCredential.getCredential();
            List<PasswordValidationError> validatePassword = PasswordMetrics.validatePassword(this.mMinMetrics, this.mMinComplexity, !this.mIsAlphaMode, credential);
            this.mValidationErrors = validatePassword;
            if (validatePassword.isEmpty() && this.mLockPatternUtils.checkPasswordHistory(credential, getPasswordHistoryHashFactor(), this.mPrivacyUserId)) {
                this.mValidationErrors = Collections.singletonList(new PasswordValidationError(13));
            }
            return this.mValidationErrors.isEmpty();
        }

        private byte[] getPasswordHistoryHashFactor() {
            if (this.mPasswordHistoryHashFactor == null) {
                this.mPasswordHistoryHashFactor = this.mLockPatternUtils.getPasswordHistoryHashFactor(LockscreenCredential.createNone(), this.mPrivacyUserId);
            }
            return this.mPasswordHistoryHashFactor;
        }

        public void handleNext() {
            LockscreenCredential lockscreenCredential;
            Editable text = this.mCurrentPasswordEntry.getText();
            if (!TextUtils.isEmpty(text)) {
                if (this.mIsAlphaMode) {
                    lockscreenCredential = LockscreenCredential.createPassword(text);
                } else {
                    lockscreenCredential = LockscreenCredential.createPin(text);
                }
                this.mChosenPassword = lockscreenCredential;
                Stage stage = this.mUiStage;
                if (stage == Stage.Introduction) {
                    if (validatePassword(lockscreenCredential)) {
                        LockscreenCredential lockscreenCredential2 = this.mChosenPassword;
                        this.mFirstPassword = lockscreenCredential2;
                        startCheckPassword(lockscreenCredential2);
                        return;
                    }
                    this.mChosenPassword.zeroize();
                } else if (stage != Stage.NeedToConfirm) {
                } else {
                    if (!lockscreenCredential.equals(this.mFirstPassword)) {
                        Editable text2 = this.mCurrentPasswordEntry.getText();
                        if (text2 != null) {
                            Selection.setSelection(text2, 0, text2.length());
                        }
                        updateStage(Stage.ConfirmWrong);
                        this.mChosenPassword.zeroize();
                    } else if (this.mIsCreatePrivacySpace) {
                        startSaveAndFinish();
                    } else {
                        startSaveAndFinishModify();
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void setNextEnabled(boolean z) {
            this.mNextButton.setEnabled(z);
        }

        /* access modifiers changed from: protected */
        public void setNextText(int i) {
            this.mNextButton.setText(getActivity(), i);
        }

        /* access modifiers changed from: protected */
        public void onSkipOrClearButtonClick(View view) {
            this.mCurrentPasswordEntry.setText("");
        }

        /* access modifiers changed from: protected */
        public void onNextButtonClick(View view) {
            handleNext();
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 0 && i != 6 && i != 5) {
                return false;
            }
            handleNext();
            return true;
        }

        /* access modifiers changed from: package-private */
        public String[] convertErrorCodeToMessages() {
            int i;
            int i2;
            int i3;
            ArrayList arrayList = new ArrayList();
            for (PasswordValidationError next : this.mValidationErrors) {
                switch (next.errorCode) {
                    case 2:
                        arrayList.add(getString(C1992R$string.lockpassword_illegal_character));
                        break;
                    case 3:
                        Resources resources = getResources();
                        if (this.mIsAlphaMode) {
                            i = C1990R$plurals.lockpassword_password_too_short;
                        } else {
                            i = C1990R$plurals.lockpassword_pin_too_short;
                        }
                        int i4 = next.requirement;
                        arrayList.add(resources.getQuantityString(i, i4, new Object[]{Integer.valueOf(i4)}));
                        break;
                    case 4:
                        Resources resources2 = getResources();
                        if (this.mIsAlphaMode) {
                            i2 = C1990R$plurals.lockpassword_password_too_long;
                        } else {
                            i2 = C1990R$plurals.lockpassword_pin_too_long;
                        }
                        int i5 = next.requirement;
                        arrayList.add(resources2.getQuantityString(i2, i5 + 1, new Object[]{Integer.valueOf(i5 + 1)}));
                        break;
                    case 5:
                        arrayList.add(getString(C1992R$string.lockpassword_pin_no_sequential_digits));
                        break;
                    case 6:
                        Resources resources3 = getResources();
                        int i6 = C1990R$plurals.lockpassword_password_requires_letters;
                        int i7 = next.requirement;
                        arrayList.add(resources3.getQuantityString(i6, i7, new Object[]{Integer.valueOf(i7)}));
                        break;
                    case 7:
                        Resources resources4 = getResources();
                        int i8 = C1990R$plurals.lockpassword_password_requires_uppercase;
                        int i9 = next.requirement;
                        arrayList.add(resources4.getQuantityString(i8, i9, new Object[]{Integer.valueOf(i9)}));
                        break;
                    case 8:
                        Resources resources5 = getResources();
                        int i10 = C1990R$plurals.lockpassword_password_requires_lowercase;
                        int i11 = next.requirement;
                        arrayList.add(resources5.getQuantityString(i10, i11, new Object[]{Integer.valueOf(i11)}));
                        break;
                    case 9:
                        Resources resources6 = getResources();
                        int i12 = C1990R$plurals.lockpassword_password_requires_numeric;
                        int i13 = next.requirement;
                        arrayList.add(resources6.getQuantityString(i12, i13, new Object[]{Integer.valueOf(i13)}));
                        break;
                    case 10:
                        Resources resources7 = getResources();
                        int i14 = C1990R$plurals.lockpassword_password_requires_symbols;
                        int i15 = next.requirement;
                        arrayList.add(resources7.getQuantityString(i14, i15, new Object[]{Integer.valueOf(i15)}));
                        break;
                    case 11:
                        Resources resources8 = getResources();
                        int i16 = C1990R$plurals.lockpassword_password_requires_nonletter;
                        int i17 = next.requirement;
                        arrayList.add(resources8.getQuantityString(i16, i17, new Object[]{Integer.valueOf(i17)}));
                        break;
                    case 12:
                        Resources resources9 = getResources();
                        int i18 = C1990R$plurals.lockpassword_password_requires_nonnumerical;
                        int i19 = next.requirement;
                        arrayList.add(resources9.getQuantityString(i18, i19, new Object[]{Integer.valueOf(i19)}));
                        break;
                    case 13:
                        if (this.mIsAlphaMode) {
                            i3 = C1992R$string.lockpassword_password_recently_used;
                        } else {
                            i3 = C1992R$string.lockpassword_pin_recently_used;
                        }
                        arrayList.add(getString(i3));
                        break;
                    default:
                        Log.wtf("PrivacySpaceChooseLockPassword", "unknown error validating password: " + next);
                        break;
                }
            }
            return (String[]) arrayList.toArray(new String[0]);
        }

        /* access modifiers changed from: protected */
        public void updateUi() {
            LockscreenCredential lockscreenCredential;
            if (this.mIsAlphaMode) {
                lockscreenCredential = LockscreenCredential.createPasswordOrNone(this.mCurrentPasswordEntry.getText());
            } else {
                lockscreenCredential = LockscreenCredential.createPinOrNone(this.mCurrentPasswordEntry.getText());
            }
            int size = lockscreenCredential.size();
            if (this.mUiStage == Stage.Introduction) {
                boolean validatePassword = validatePassword(lockscreenCredential);
                this.mPasswordRequirementAdapter.setRequirements(convertErrorCodeToMessages());
                setNextEnabled(validatePassword);
            } else {
                this.mPasswordRestrictionView.setVisibility(8);
                setHeaderText(getString(this.mUiStage.getHint(this.mIsAlphaMode, getStageType())));
                if (this.mPinForSixBit) {
                    setNextEnabled(size == 6);
                } else {
                    setNextEnabled(size >= 4);
                }
                this.mSkipOrClearButton.setVisibility(toVisibility(size > 0));
            }
            int message = this.mUiStage.getMessage(this.mIsAlphaMode, getStageType());
            if (message != 0) {
                this.mMessage.setVisibility(0);
                this.mMessage.setText(message);
            } else {
                this.mMessage.setVisibility(4);
            }
            setNextText(this.mUiStage.buttonText);
            this.mCurrentEntryInputDisabler.setInputEnabled(true);
            lockscreenCredential.zeroize();
        }

        private void setHeaderText(String str) {
            if (TextUtils.isEmpty(this.mLayout.getHeaderText()) || !this.mLayout.getHeaderText().toString().equals(str)) {
                this.mLayout.setHeaderText((CharSequence) str);
            }
        }

        public void afterTextChanged(Editable editable) {
            if (this.mUiStage == Stage.ConfirmWrong) {
                this.mUiStage = Stage.NeedToConfirm;
            }
            this.mTextChangedHandler.notifyAfterTextChanged();
        }

        private void startCheckPassword(LockscreenCredential lockscreenCredential) {
            int credentialOwnerProfile = UserManager.get(getActivity()).getCredentialOwnerProfile(this.mUserId);
            LockPatternUtils lockPatternUtils = new LockPatternUtils(getActivity());
            this.mLockPatternUtils = lockPatternUtils;
            LockPatternChecker.checkCredential(lockPatternUtils, lockscreenCredential, credentialOwnerProfile, new C1957x55c90405(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$startCheckPassword$0(boolean z, int i) {
            onPasswordChecked(z);
        }

        private void onPasswordChecked(boolean z) {
            String str;
            this.mCurrentEntryInputDisabler.setInputEnabled(true);
            LockscreenCredential lockscreenCredential = this.mMainChosenPassword;
            boolean z2 = lockscreenCredential != null && lockscreenCredential.equals(this.mFirstPassword);
            if (z || z2) {
                if (this.mIsAlphaMode) {
                    str = getString(C1992R$string.lockpassword_privacy_password_same_as_main_space);
                } else {
                    str = getString(C1992R$string.lockpassword_privacy_pin_same_as_main_space);
                }
                this.mPasswordRequirementAdapter.setRequirements(new String[]{str});
                return;
            }
            this.mCurrentPasswordEntry.setText("");
            updateStage(Stage.NeedToConfirm);
        }

        private void startSaveAndFinish() {
            startActivityForResult(getRedactionInterstitialIntent(getActivity()), R$styleable.Constraint_pathMotionArc);
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
                if (PrivacySpaceChooseLockPasswordFragment.this.getActivity() != null && message.what == 1) {
                    PrivacySpaceChooseLockPasswordFragment.this.updateUi();
                }
            }
        }

        public void onChosenLockSaveFinished(boolean z, Intent intent) {
            getActivity().setResult(1, intent);
            LockscreenCredential lockscreenCredential = this.mChosenPassword;
            if (lockscreenCredential != null) {
                lockscreenCredential.zeroize();
            }
            LockscreenCredential lockscreenCredential2 = this.mMainChosenPassword;
            if (lockscreenCredential2 != null) {
                lockscreenCredential2.zeroize();
            }
            LockscreenCredential lockscreenCredential3 = this.mMainUserPassword;
            if (lockscreenCredential3 != null) {
                lockscreenCredential3.zeroize();
            }
            LockscreenCredential lockscreenCredential4 = this.mPrivacyUserPassword;
            if (lockscreenCredential4 != null) {
                lockscreenCredential4.zeroize();
            }
            LockscreenCredential lockscreenCredential5 = this.mFirstPassword;
            if (lockscreenCredential5 != null) {
                lockscreenCredential5.zeroize();
            }
            this.mCurrentPasswordEntry.setText("");
            getActivity().finish();
        }

        private void startSaveAndFinishModify() {
            if (this.mSaveAndFinishWorker != null) {
                Log.w("PrivacySpaceChooseLockPassword", "startSaveAndFinish with an existing SaveAndFinishWorker.");
                return;
            }
            this.mCurrentEntryInputDisabler.setInputEnabled(false);
            setNextEnabled(false);
            SaveAndFinishWorker saveAndFinishWorker = new SaveAndFinishWorker();
            this.mSaveAndFinishWorker = saveAndFinishWorker;
            saveAndFinishWorker.setListener(this);
            getFragmentManager().beginTransaction().add((Fragment) this.mSaveAndFinishWorker, "save_and_finish_worker").commit();
            getFragmentManager().executePendingTransactions();
            getActivity().getIntent().getBooleanExtra("extra_require_password", true);
            this.mSaveAndFinishWorker.start(this.mLockPatternUtils, this.mMainChosenPassword, this.mMainUserPassword, this.mChosenPassword, this.mPrivacyUserPassword, this.mUserId, this.mPrivacyUserId, this.mPinForSixBit);
        }
    }

    public static class SaveAndFinishWorker extends SaveChosenLockWorkerBase {
        private boolean isSixPinLock;
        private LockscreenCredential mMainChosenPassword;
        private LockscreenCredential mMainCurrentCredential;
        private LockscreenCredential mPrivacyChosenPassword;
        private LockscreenCredential mPrivacyCurrentCredential;
        private int mPrivacyUserId;

        public void start(LockPatternUtils lockPatternUtils, LockscreenCredential lockscreenCredential, LockscreenCredential lockscreenCredential2, LockscreenCredential lockscreenCredential3, LockscreenCredential lockscreenCredential4, int i, int i2, boolean z) {
            prepare(lockPatternUtils, false, false, i);
            if (lockscreenCredential == null) {
                lockscreenCredential = LockscreenCredential.createNone();
            }
            this.mMainChosenPassword = lockscreenCredential;
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
            this.isSixPinLock = z;
            start();
        }

        /* access modifiers changed from: protected */
        public Pair<Boolean, Intent> saveAndVerifyInBackground() {
            Log.i("PrivacySpaceChooseLockPassword", "save password,mUserId:" + this.mUserId + ",mPrivacyUserId:" + this.mPrivacyUserId);
            boolean lockCredential = this.mUtils.setLockCredential(this.mMainChosenPassword, this.mMainCurrentCredential, this.mUserId);
            boolean lockCredential2 = this.mUtils.setLockCredential(this.mPrivacyChosenPassword, this.mPrivacyCurrentCredential, this.mPrivacyUserId);
            Log.i("PrivacySpaceChooseLockPassword", "save password,mainSuccess:" + lockCredential + ",privacySuccess:" + lockCredential2);
            boolean z = lockCredential && lockCredential2;
            if (z) {
                this.mUtils.setSixBitForPin(this.isSixPinLock, this.mUserId);
                this.mUtils.setSixBitForPin(this.isSixPinLock, this.mPrivacyUserId);
            }
            return Pair.create(Boolean.valueOf(z), (Object) null);
        }
    }
}

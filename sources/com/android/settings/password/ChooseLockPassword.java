package com.android.settings.password;

import android.app.Dialog;
import android.app.admin.PasswordMetrics;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Insets;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserManager;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImeAwareEditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockscreenCredential;
import com.android.internal.widget.PasswordValidationError;
import com.android.internal.widget.TextViewInputDisabler;
import com.android.internal.widget.VerifyCredentialResponse;
import com.android.settings.C1978R$array;
import com.android.settings.C1982R$dimen;
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
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.notification.RedactionInterstitial;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.password.SaveChosenLockWorkerBase;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;
import com.google.android.setupdesign.util.ThemeHelper;
import com.motorola.settings.security.screenlock.SettingsSetUpActivity;
import com.motorola.settings.widget.PasswordView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChooseLockPassword extends SettingsSetUpActivity {
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
            Intent intent = new Intent(context, ChooseLockPassword.class);
            this.mIntent = intent;
            intent.putExtra("confirm_credentials", false);
            intent.putExtra("extra_require_password", false);
        }

        public IntentBuilder setPasswordType(int i) {
            this.mIntent.putExtra("lockscreen.password_type", i);
            return this;
        }

        public IntentBuilder setUserId(int i) {
            this.mIntent.putExtra("android.intent.extra.USER_ID", i);
            return this;
        }

        public IntentBuilder setRequestGatekeeperPasswordHandle(boolean z) {
            this.mIntent.putExtra("request_gk_pw_handle", z);
            return this;
        }

        public IntentBuilder setPassword(LockscreenCredential lockscreenCredential) {
            this.mIntent.putExtra("password", lockscreenCredential);
            return this;
        }

        public IntentBuilder setForFingerprint(boolean z) {
            this.mIntent.putExtra("for_fingerprint", z);
            return this;
        }

        public IntentBuilder setForFace(boolean z) {
            this.mIntent.putExtra("for_face", z);
            return this;
        }

        public IntentBuilder setForBiometrics(boolean z) {
            this.mIntent.putExtra("for_biometrics", z);
            return this;
        }

        public IntentBuilder setIsCreatePrivacySpace(boolean z) {
            this.mIntent.putExtra("setting_from_privacy_space", z);
            return this;
        }

        public IntentBuilder setIsPrivacySpaceCreated(boolean z) {
            this.mIntent.putExtra("whether_privacy_space_created", z);
            return this;
        }

        public IntentBuilder setIsMainSpaceLockTypeChange(boolean z) {
            this.mIntent.putExtra("main_space_lock_type_change", z);
            return this;
        }

        public IntentBuilder setPasswordRequirement(int i, PasswordMetrics passwordMetrics) {
            this.mIntent.putExtra("min_complexity", i);
            this.mIntent.putExtra("min_metrics", passwordMetrics);
            return this;
        }

        public IntentBuilder setProfileToUnify(int i, LockscreenCredential lockscreenCredential) {
            this.mIntent.putExtra("unification_profile_id", i);
            this.mIntent.putExtra("unification_profile_credential", lockscreenCredential);
            return this;
        }

        public Intent build() {
            return this.mIntent;
        }
    }

    /* access modifiers changed from: protected */
    public boolean isValidFragment(String str) {
        return ChooseLockPasswordFragment.class.getName().equals(str);
    }

    /* access modifiers changed from: package-private */
    public Class<? extends Fragment> getFragmentClass() {
        return ChooseLockPasswordFragment.class;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setTheme(SetupWizardUtils.getTheme(this, getIntent()));
        ThemeHelper.trySetDynamicColor(this);
        super.onCreate(bundle);
        findViewById(C1985R$id.content_parent).setFitsSystemWindows(false);
    }

    public static class ChooseLockPasswordFragment extends InstrumentedFragment implements TextView.OnEditorActionListener, TextWatcher, SaveChosenLockWorkerBase.Listener {
        private LockscreenCredential mChosenPassword;
        /* access modifiers changed from: private */
        public LockscreenCredential mCurrentCredential;
        /* access modifiers changed from: private */
        public TextViewInputDisabler mCurrentEntryInputDisabler;
        /* access modifiers changed from: private */
        public ImeAwareEditText mCurrentPasswordEntry;
        private LockscreenCredential mFirstPassword;
        protected boolean mForBiometrics;
        protected boolean mForFace;
        protected boolean mForFingerprint;
        /* access modifiers changed from: private */
        public TextView mIntroduceView;
        protected boolean mIsAlphaMode;
        private boolean mIsCreatePrivacySpace = false;
        /* access modifiers changed from: private */
        public boolean mIsMainSpaceLockTypeChange = false;
        private boolean mIsMainSpacePINLockTypeChange = false;
        protected boolean mIsManagedProfile;
        /* access modifiers changed from: private */
        public boolean mIsPrivacySpaceCreated = false;
        private boolean mIsPrivacyUser;
        private GlifLayout mLayout;
        private LockPatternUtils mLockPatternUtils;
        private TextView mMessage;
        private int mMinComplexity = 0;
        /* access modifiers changed from: private */
        public PasswordMetrics mMinMetrics;
        private FooterButton mNextButton;
        /* access modifiers changed from: private */
        public ImeAwareEditText mPasswordEntry;
        /* access modifiers changed from: private */
        public TextViewInputDisabler mPasswordEntryInputDisabler;
        private byte[] mPasswordHistoryHashFactor;
        private PasswordRequirementAdapter mPasswordRequirementAdapter;
        private RecyclerView mPasswordRestrictionView;
        private int mPasswordType = 131072;
        /* access modifiers changed from: private */
        public boolean mPinForSixBit = false;
        private Spinner mPinTypeSelector;
        private boolean mRequestGatekeeperPassword;
        private SaveAndFinishWorker mSaveAndFinishWorker;
        /* access modifiers changed from: private */
        public PasswordView mSixBitEntry;
        /* access modifiers changed from: private */
        public TextViewInputDisabler mSixBitEntryInputDisabler;
        protected FooterButton mSkipOrClearButton;
        private TextChangedHandler mTextChangedHandler;
        protected Stage mUiStage = Stage.Introduction;
        private int mUnificationProfileId = -10000;
        protected int mUserId;
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
            Introduction(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, C1992R$string.lockpassword_choose_pin_description, r18, C1992R$string.next_label),
            NeedToConfirm(r29, r27, r29, r29, r29, r29, r29, r36, r34, r36, r36, r36, r36, r36, 0, 0, 0, 0, r44),
            ConfirmWrong(r48, r49, r50, r51, r51, r51, r51, r55, r56, r57, r58, r59, r58, r58, 0, 0, 0, 0, r44);
            
            public final int alphaHint;
            public final int alphaHintForBiometrics;
            public final int alphaHintForFace;
            public final int alphaHintForFingerprint;
            public final int alphaHintForMainSpace;
            public final int alphaHintForPrivacySpace;
            public final int alphaHintForProfile;
            public final int alphaMessage;
            public final int alphaMessageForBiometrics;
            public final int buttonText;
            public final int numericHint;
            public final int numericHintForBiometrics;
            public final int numericHintForFace;
            public final int numericHintForFingerprint;
            public final int numericHintForMainSpace;
            public final int numericHintForPrivacySpace;
            public final int numericHintForProfile;
            public final int numericMessage;
            public final int numericMessageForBiometrics;

            private Stage(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14, int i15, int i16, int i17, int i18, int i19) {
                this.alphaHint = i;
                this.alphaHintForProfile = i2;
                this.alphaHintForFingerprint = i3;
                this.alphaHintForFace = i4;
                this.alphaHintForBiometrics = i5;
                this.alphaHintForMainSpace = i6;
                this.alphaHintForPrivacySpace = i7;
                this.numericHint = i8;
                this.numericHintForProfile = i9;
                this.numericHintForFingerprint = i10;
                this.numericHintForFace = i11;
                this.numericHintForBiometrics = i12;
                this.numericHintForMainSpace = i13;
                this.numericHintForPrivacySpace = i14;
                this.alphaMessage = i15;
                this.alphaMessageForBiometrics = i16;
                this.numericMessage = i17;
                this.numericMessageForBiometrics = i18;
                this.buttonText = i19;
            }

            public int getHint(boolean z, int i, boolean z2, boolean z3, boolean z4) {
                if (z) {
                    if (i == 1) {
                        return this.alphaHintForFingerprint;
                    }
                    if (i == 2) {
                        return this.alphaHintForFace;
                    }
                    if (i == 3) {
                        return this.alphaHintForBiometrics;
                    }
                    if (z4) {
                        return this.numericHintForPrivacySpace;
                    }
                    if (z3) {
                        return this.alphaHintForMainSpace;
                    }
                    return z2 ? this.alphaHintForProfile : this.alphaHint;
                } else if (i == 1) {
                    return this.numericHintForFingerprint;
                } else {
                    if (i == 2) {
                        return this.numericHintForFace;
                    }
                    if (i == 3) {
                        return this.numericHintForBiometrics;
                    }
                    if (z4) {
                        return this.numericHintForPrivacySpace;
                    }
                    if (z3) {
                        return this.numericHintForMainSpace;
                    }
                    return z2 ? this.numericHintForProfile : this.numericHint;
                }
            }

            public int getMessage(boolean z, int i) {
                return (i == 1 || i == 2 || i == 3) ? z ? this.alphaMessageForBiometrics : this.numericMessageForBiometrics : z ? this.alphaMessage : this.numericMessage;
            }
        }

        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mLockPatternUtils = new LockPatternUtils(getActivity());
            Intent intent = getActivity().getIntent();
            if (getActivity() instanceof ChooseLockPassword) {
                this.mUserId = Utils.getUserIdFromBundle(getActivity(), intent.getExtras());
                this.mIsManagedProfile = UserManager.get(getActivity()).isManagedProfile(this.mUserId);
                this.mForFingerprint = intent.getBooleanExtra("for_fingerprint", false);
                this.mForFace = intent.getBooleanExtra("for_face", false);
                this.mForBiometrics = intent.getBooleanExtra("for_biometrics", false);
                this.mPasswordType = intent.getIntExtra("lockscreen.password_type", 131072);
                this.mUnificationProfileId = intent.getIntExtra("unification_profile_id", -10000);
                this.mCurrentCredential = intent.getParcelableExtra("password");
                this.mRequestGatekeeperPassword = intent.getBooleanExtra("request_gk_pw_handle", false);
                this.mIsCreatePrivacySpace = intent.getBooleanExtra("setting_from_privacy_space", false);
                this.mIsPrivacySpaceCreated = intent.getBooleanExtra("whether_privacy_space_created", false);
                this.mIsMainSpaceLockTypeChange = intent.getBooleanExtra("main_space_lock_type_change", false);
                int privacySpaceUserId = UserManager.get(getActivity()).getPrivacySpaceUserId();
                this.mIsPrivacyUser = privacySpaceUserId > 0 && privacySpaceUserId == this.mUserId;
                this.mMinComplexity = intent.getIntExtra("min_complexity", 0);
                PasswordMetrics parcelableExtra = intent.getParcelableExtra("min_metrics");
                this.mMinMetrics = parcelableExtra;
                if (parcelableExtra == null) {
                    this.mMinMetrics = new PasswordMetrics(-1);
                }
                if (intent.getBooleanExtra("for_cred_req_boot", false)) {
                    SaveAndFinishWorker saveAndFinishWorker = new SaveAndFinishWorker();
                    boolean booleanExtra = getActivity().getIntent().getBooleanExtra("extra_require_password", true);
                    LockscreenCredential parcelableExtra2 = intent.getParcelableExtra("password");
                    LockPatternUtils lockPatternUtils = new LockPatternUtils(getActivity());
                    saveAndFinishWorker.setBlocking(true);
                    saveAndFinishWorker.setListener(this);
                    saveAndFinishWorker.start(lockPatternUtils, booleanExtra, false, parcelableExtra2, parcelableExtra2, this.mUserId, this.mPinForSixBit);
                }
                this.mTextChangedHandler = new TextChangedHandler();
                return;
            }
            throw new SecurityException("Fragment contained in wrong activity");
        }

        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            return layoutInflater.inflate(C1987R$layout.choose_lock_password, viewGroup, false);
        }

        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            this.mLayout = (GlifLayout) view;
            ((ViewGroup) view.findViewById(C1985R$id.password_container)).setOpticalInsets(Insets.NONE);
            FooterBarMixin footerBarMixin = (FooterBarMixin) this.mLayout.getMixin(FooterBarMixin.class);
            footerBarMixin.setSecondaryButton(new FooterButton.Builder(getActivity()).setText(C1992R$string.lockpassword_clear_label).setListener(new C1220xcfed0644(this)).setButtonType(7).setTheme(C1993R$style.SudGlifButton_Secondary).build());
            footerBarMixin.setPrimaryButton(new FooterButton.Builder(getActivity()).setText(C1992R$string.next_label).setListener(new C1219xcfed0643(this)).setButtonType(5).setTheme(C1993R$style.SudGlifButton_Primary).build());
            this.mSkipOrClearButton = footerBarMixin.getSecondaryButton();
            this.mNextButton = footerBarMixin.getPrimaryButton();
            this.mMessage = (TextView) view.findViewById(C1985R$id.sud_layout_description);
            if (this.mForFingerprint) {
                this.mLayout.setIcon(getActivity().getDrawable(C1983R$drawable.ic_fingerprint_header));
            } else if (this.mForFace) {
                this.mLayout.setIcon(getActivity().getDrawable(C1983R$drawable.ic_face_header));
            } else if (this.mForBiometrics) {
                this.mLayout.setIcon(getActivity().getDrawable(C1983R$drawable.ic_lock));
            }
            int i = this.mPasswordType;
            this.mIsAlphaMode = 262144 == i || 327680 == i || 393216 == i;
            setupPasswordRequirementsView(view);
            this.mPasswordRestrictionView.setLayoutManager(new LinearLayoutManager(getActivity()));
            ImeAwareEditText findViewById = view.findViewById(C1985R$id.password_entry);
            this.mPasswordEntry = findViewById;
            initPasswordEntry(findViewById);
            TextViewInputDisabler textViewInputDisabler = new TextViewInputDisabler(this.mPasswordEntry);
            this.mPasswordEntryInputDisabler = textViewInputDisabler;
            this.mCurrentPasswordEntry = this.mPasswordEntry;
            this.mCurrentEntryInputDisabler = textViewInputDisabler;
            if (Build.IS_PRC_PRODUCT) {
                this.mIntroduceView = (TextView) view.findViewById(C1985R$id.password_description);
                this.mSixBitEntry = (PasswordView) view.findViewById(C1985R$id.password_entry_six_bit);
                this.mPinTypeSelector = (Spinner) view.findViewById(C1985R$id.password_selector);
                if (!this.mIsAlphaMode) {
                    LockscreenCredential lockscreenCredential = this.mCurrentCredential;
                    if (lockscreenCredential == null || lockscreenCredential.getType() != 3) {
                        this.mPinForSixBit = true;
                    } else {
                        this.mPinForSixBit = this.mLockPatternUtils.isSixBitForPinLock(this.mUserId);
                    }
                    initPrcContent();
                }
                if (this.mIsPrivacyUser) {
                    this.mIntroduceView.setVisibility(8);
                    this.mPinTypeSelector.setVisibility(8);
                    if (this.mIsAlphaMode || !this.mLockPatternUtils.isSixBitForPinLock(this.mUserId)) {
                        this.mMinMetrics.length = 4;
                        this.mPinForSixBit = false;
                        this.mSixBitEntry.setVisibility(8);
                        this.mPasswordEntry.setVisibility(0);
                        this.mCurrentPasswordEntry = this.mPasswordEntry;
                        this.mCurrentEntryInputDisabler = this.mPasswordEntryInputDisabler;
                    } else {
                        this.mMinMetrics.length = 6;
                        this.mPinForSixBit = true;
                        this.mSixBitEntry.setVisibility(0);
                        this.mPasswordEntry.setVisibility(8);
                        this.mCurrentPasswordEntry = this.mSixBitEntry;
                        this.mCurrentEntryInputDisabler = this.mSixBitEntryInputDisabler;
                    }
                }
            }
            FragmentActivity activity = getActivity();
            boolean booleanExtra = getActivity().getIntent().getBooleanExtra("confirm_credentials", true);
            if (bundle == null) {
                updateStage(Stage.Introduction);
                if (booleanExtra) {
                    new ChooseLockSettingsHelper.Builder(getActivity()).setRequestCode(58).setTitle(getString(C1992R$string.unlock_set_unlock_launch_picker_title)).setReturnCredentials(true).setRequestGatekeeperPasswordHandle(this.mRequestGatekeeperPassword).setUserId(this.mUserId).show();
                }
            } else {
                this.mFirstPassword = bundle.getParcelable("first_password");
                String string = bundle.getString("ui_stage");
                if (string != null) {
                    Stage valueOf = Stage.valueOf(string);
                    this.mUiStage = valueOf;
                    updateStage(valueOf);
                }
                if (Build.IS_PRC_PRODUCT) {
                    this.mPinForSixBit = bundle.getBoolean("current_pinlock_type");
                }
                this.mCurrentCredential = bundle.getParcelable("current_credential");
                this.mSaveAndFinishWorker = (SaveAndFinishWorker) getFragmentManager().findFragmentByTag("save_and_finish_worker");
            }
            if (activity instanceof SettingsActivity) {
                SettingsActivity settingsActivity = (SettingsActivity) activity;
                int hint = Stage.Introduction.getHint(this.mIsAlphaMode, getStageType(), this.mIsManagedProfile, this.mIsCreatePrivacySpace || this.mIsPrivacySpaceCreated, this.mIsPrivacyUser);
                settingsActivity.setTitle(hint);
                this.mLayout.setHeaderText(hint);
            }
        }

        /* JADX WARNING: type inference failed for: r2v0, types: [android.widget.TextView, com.motorola.settings.widget.PasswordView] */
        private void initPrcContent() {
            this.mIntroduceView.setVisibility(0);
            initPasswordEntry(this.mSixBitEntry);
            this.mSixBitEntryInputDisabler = new TextViewInputDisabler(this.mSixBitEntry);
            final List asList = Arrays.asList(getContext().getResources().getStringArray(C1978R$array.entries_password_pin_type));
            C12171 r2 = new ArrayAdapter<String>(getContext(), C1987R$layout.arrow_spinner_layout, C1985R$id.spinner_text, asList) {
                public View getDropDownView(int i, View view, ViewGroup viewGroup) {
                    View dropDownView = super.getDropDownView(i, view, viewGroup);
                    TextView textView = (TextView) dropDownView.findViewById(C1985R$id.spinner_text);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) textView.getLayoutParams();
                    int dimensionPixelSize = ChooseLockPasswordFragment.this.getResources().getDimensionPixelSize(C1982R$dimen.settings_spinner_dropdown_margin);
                    if (i == 0) {
                        layoutParams.setMargins(0, dimensionPixelSize, 0, 0);
                        textView.setLayoutParams(layoutParams);
                    } else if (i == asList.size() - 1) {
                        layoutParams.setMargins(0, 0, 0, dimensionPixelSize);
                        textView.setLayoutParams(layoutParams);
                    }
                    return dropDownView;
                }
            };
            r2.setDropDownViewResource(C1987R$layout.spinner_list_item);
            this.mPinTypeSelector.setAdapter(r2);
            this.mPinTypeSelector.setVisibility(0);
            this.mPinTypeSelector.setSelection(this.mPinForSixBit ^ true ? 1 : 0);
            this.mPinTypeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                    if (ChooseLockPasswordFragment.this.mIsPrivacySpaceCreated && !ChooseLockPasswordFragment.this.mIsMainSpaceLockTypeChange && ChooseLockPasswordFragment.this.mCurrentCredential != null && ChooseLockPasswordFragment.this.mCurrentCredential.getType() == 3) {
                        ChooseLockPasswordFragment.this.isMainSpacePINLockTypeChange(i);
                    }
                    if (i == 0) {
                        ChooseLockPasswordFragment.this.mMinMetrics.length = 6;
                        ChooseLockPasswordFragment chooseLockPasswordFragment = ChooseLockPasswordFragment.this;
                        ImeAwareEditText unused = chooseLockPasswordFragment.mCurrentPasswordEntry = chooseLockPasswordFragment.mSixBitEntry;
                        ChooseLockPasswordFragment chooseLockPasswordFragment2 = ChooseLockPasswordFragment.this;
                        TextViewInputDisabler unused2 = chooseLockPasswordFragment2.mCurrentEntryInputDisabler = chooseLockPasswordFragment2.mSixBitEntryInputDisabler;
                        ChooseLockPasswordFragment.this.mIntroduceView.setText(C1992R$string.security_password_pin_six_bit_selector_introduce);
                        boolean unused3 = ChooseLockPasswordFragment.this.mPinForSixBit = true;
                        ChooseLockPasswordFragment.this.mSixBitEntry.setVisibility(0);
                        ChooseLockPasswordFragment.this.mPasswordEntry.setVisibility(8);
                        ChooseLockPasswordFragment.this.mPasswordEntry.setText("");
                        ChooseLockPasswordFragment.this.mSixBitEntry.requestFocus();
                        ChooseLockPasswordFragment.this.mSixBitEntry.scheduleShowSoftInput();
                        return;
                    }
                    ChooseLockPasswordFragment.this.mMinMetrics.length = 4;
                    ChooseLockPasswordFragment chooseLockPasswordFragment3 = ChooseLockPasswordFragment.this;
                    ImeAwareEditText unused4 = chooseLockPasswordFragment3.mCurrentPasswordEntry = chooseLockPasswordFragment3.mPasswordEntry;
                    ChooseLockPasswordFragment chooseLockPasswordFragment4 = ChooseLockPasswordFragment.this;
                    TextViewInputDisabler unused5 = chooseLockPasswordFragment4.mCurrentEntryInputDisabler = chooseLockPasswordFragment4.mPasswordEntryInputDisabler;
                    String string = ChooseLockPasswordFragment.this.getString(C1992R$string.security_password_pin_custom_selector_introduce);
                    SpannableString spannableString = new SpannableString(string);
                    spannableString.setSpan(new ImageSpan(ChooseLockPasswordFragment.this.getContext(), C1983R$drawable.ic_keyboard_tab_12sp, 2), string.length() - 1, string.length(), 17);
                    ChooseLockPasswordFragment.this.mIntroduceView.setText(spannableString);
                    boolean unused6 = ChooseLockPasswordFragment.this.mPinForSixBit = false;
                    ChooseLockPasswordFragment.this.mSixBitEntry.setVisibility(8);
                    ChooseLockPasswordFragment.this.mPasswordEntry.setVisibility(0);
                    ChooseLockPasswordFragment.this.mSixBitEntry.setText("");
                    ChooseLockPasswordFragment.this.mPasswordEntry.requestFocus();
                    ChooseLockPasswordFragment.this.mPasswordEntry.scheduleShowSoftInput();
                }
            });
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

        /* access modifiers changed from: private */
        public void isMainSpacePINLockTypeChange(int i) {
            if (i != (!this.mLockPatternUtils.isSixBitForPinLock(this.mUserId))) {
                showMainSpaceLockTypeChangeWarningDialog();
                this.mIsMainSpacePINLockTypeChange = true;
                return;
            }
            this.mIsMainSpacePINLockTypeChange = false;
        }

        public void onDestroy() {
            super.onDestroy();
            LockscreenCredential lockscreenCredential = this.mCurrentCredential;
            if (lockscreenCredential != null) {
                lockscreenCredential.zeroize();
            }
            System.gc();
            System.runFinalization();
            System.gc();
        }

        /* access modifiers changed from: protected */
        public int getStageType() {
            if (this.mForFingerprint) {
                return 1;
            }
            if (this.mForFace) {
                return 2;
            }
            return this.mForBiometrics ? 3 : 0;
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
            if (Build.IS_PRC_PRODUCT) {
                bundle.putBoolean("current_pinlock_type", this.mPinForSixBit);
            }
            LockscreenCredential lockscreenCredential = this.mCurrentCredential;
            if (lockscreenCredential != null) {
                bundle.putParcelable("current_credential", lockscreenCredential.duplicate());
            }
        }

        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i == 58) {
                if (i2 != -1) {
                    getActivity().setResult(1);
                    getActivity().finish();
                    return;
                }
                this.mCurrentCredential = intent.getParcelableExtra("password");
            }
        }

        /* access modifiers changed from: protected */
        public Intent getRedactionInterstitialIntent(Context context) {
            return RedactionInterstitial.createStartIntent(context, this.mUserId);
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
            if (validatePassword.isEmpty() && this.mLockPatternUtils.checkPasswordHistory(credential, getPasswordHistoryHashFactor(), this.mUserId)) {
                this.mValidationErrors = Collections.singletonList(new PasswordValidationError(13));
            }
            return this.mValidationErrors.isEmpty();
        }

        private byte[] getPasswordHistoryHashFactor() {
            if (this.mPasswordHistoryHashFactor == null) {
                LockPatternUtils lockPatternUtils = this.mLockPatternUtils;
                LockscreenCredential lockscreenCredential = this.mCurrentCredential;
                if (lockscreenCredential == null) {
                    lockscreenCredential = LockscreenCredential.createNone();
                }
                this.mPasswordHistoryHashFactor = lockPatternUtils.getPasswordHistoryHashFactor(lockscreenCredential, this.mUserId);
            }
            return this.mPasswordHistoryHashFactor;
        }

        public void handleNext() {
            LockscreenCredential lockscreenCredential;
            if (this.mSaveAndFinishWorker == null) {
                Editable text = this.mCurrentPasswordEntry.getText();
                if (!TextUtils.isEmpty(text)) {
                    if (this.mIsAlphaMode) {
                        lockscreenCredential = LockscreenCredential.createPassword(text);
                    } else {
                        lockscreenCredential = LockscreenCredential.createPin(text);
                    }
                    this.mChosenPassword = lockscreenCredential;
                    Stage stage = this.mUiStage;
                    boolean z = false;
                    if (stage == Stage.Introduction) {
                        if (validatePassword(lockscreenCredential)) {
                            LockscreenCredential lockscreenCredential2 = this.mChosenPassword;
                            this.mFirstPassword = lockscreenCredential2;
                            if (this.mIsPrivacySpaceCreated || this.mIsPrivacyUser) {
                                if (this.mUserId == 0) {
                                    z = true;
                                }
                                startCheckPassword(lockscreenCredential2, z);
                                return;
                            }
                            this.mCurrentPasswordEntry.setText("");
                            updateStage(Stage.NeedToConfirm);
                            return;
                        }
                        this.mChosenPassword.zeroize();
                    } else if (stage != Stage.NeedToConfirm) {
                    } else {
                        if (lockscreenCredential.equals(this.mFirstPassword)) {
                            startSaveAndFinish();
                            return;
                        }
                        Editable text2 = this.mCurrentPasswordEntry.getText();
                        if (text2 != null) {
                            Selection.setSelection(text2, 0, text2.length());
                        }
                        if (Build.IS_PRC_PRODUCT && this.mPinForSixBit) {
                            this.mSixBitEntry.resetPasswordText();
                        }
                        updateStage(Stage.ConfirmWrong);
                        this.mChosenPassword.zeroize();
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
                        if (Build.IS_PRC_PRODUCT && !this.mIsAlphaMode) {
                            break;
                        } else {
                            Resources resources = getResources();
                            if (this.mIsAlphaMode) {
                                i = C1990R$plurals.lockpassword_password_too_short;
                            } else {
                                i = C1990R$plurals.lockpassword_pin_too_short;
                            }
                            int i4 = next.requirement;
                            arrayList.add(resources.getQuantityString(i, i4, new Object[]{Integer.valueOf(i4)}));
                            break;
                        }
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
                        Log.wtf("ChooseLockPassword", "unknown error validating password: " + next);
                        break;
                }
            }
            return (String[]) arrayList.toArray(new String[0]);
        }

        /* access modifiers changed from: protected */
        public void updateUi() {
            LockscreenCredential lockscreenCredential;
            boolean z = true;
            boolean z2 = this.mSaveAndFinishWorker == null;
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
                if (!this.mIsAlphaMode && Build.IS_PRC_PRODUCT) {
                    this.mIntroduceView.setVisibility(8);
                    this.mPinTypeSelector.setVisibility(8);
                }
                this.mPasswordRestrictionView.setVisibility(8);
                setHeaderText(getString(this.mUiStage.getHint(this.mIsAlphaMode, getStageType(), this.mIsManagedProfile, this.mIsCreatePrivacySpace || this.mIsPrivacySpaceCreated, this.mIsPrivacyUser)));
                if (this.mPinForSixBit) {
                    setNextEnabled(z2 && size == 6);
                } else {
                    setNextEnabled(z2 && size >= 4);
                }
                FooterButton footerButton = this.mSkipOrClearButton;
                if (!z2 || size <= 0) {
                    z = false;
                }
                footerButton.setVisibility(toVisibility(z));
            }
            int message = this.mUiStage.getMessage(this.mIsAlphaMode, getStageType());
            if (message != 0) {
                this.mMessage.setVisibility(0);
                this.mMessage.setText(message);
            } else {
                this.mMessage.setVisibility(4);
            }
            setNextText(this.mUiStage.buttonText);
            this.mCurrentEntryInputDisabler.setInputEnabled(z2);
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

        private void startSaveAndFinish() {
            if (this.mSaveAndFinishWorker != null) {
                Log.w("ChooseLockPassword", "startSaveAndFinish with an existing SaveAndFinishWorker.");
                return;
            }
            boolean z = false;
            if (this.mIsMainSpaceLockTypeChange || this.mIsMainSpacePINLockTypeChange) {
                Intent intent = new Intent();
                if (this.mIsMainSpacePINLockTypeChange || this.mIsMainSpaceLockTypeChange) {
                    z = true;
                }
                intent.putExtra("main_space_lock_type_change", z);
                intent.putExtra("main_space_password", this.mChosenPassword);
                intent.putExtra("main_space_is_six_pin", this.mPinForSixBit);
                getActivity().setResult(1, intent);
                getActivity().finish();
                return;
            }
            this.mCurrentEntryInputDisabler.setInputEnabled(false);
            setNextEnabled(false);
            SaveAndFinishWorker saveAndFinishWorker = new SaveAndFinishWorker();
            this.mSaveAndFinishWorker = saveAndFinishWorker;
            saveAndFinishWorker.setListener(this);
            getFragmentManager().beginTransaction().add((Fragment) this.mSaveAndFinishWorker, "save_and_finish_worker").commit();
            getFragmentManager().executePendingTransactions();
            Intent intent2 = getActivity().getIntent();
            boolean booleanExtra = intent2.getBooleanExtra("extra_require_password", true);
            if (this.mUnificationProfileId != -10000) {
                LockscreenCredential parcelableExtra = intent2.getParcelableExtra("unification_profile_credential");
                try {
                    this.mSaveAndFinishWorker.setProfileToUnify(this.mUnificationProfileId, parcelableExtra);
                    if (parcelableExtra != null) {
                        parcelableExtra.close();
                    }
                } catch (Throwable th) {
                    th.addSuppressed(th);
                }
            }
            this.mSaveAndFinishWorker.start(this.mLockPatternUtils, booleanExtra, this.mRequestGatekeeperPassword, this.mChosenPassword, this.mCurrentCredential, this.mUserId, this.mPinForSixBit);
            return;
            throw th;
        }

        public void onChosenLockSaveFinished(boolean z, Intent intent) {
            Intent redactionInterstitialIntent;
            if (intent == null && this.mIsCreatePrivacySpace) {
                intent = new Intent();
                intent.putExtra("lockscreen.password_type", this.mPasswordType);
            }
            getActivity().setResult(1, intent);
            LockscreenCredential lockscreenCredential = this.mChosenPassword;
            if (lockscreenCredential != null) {
                lockscreenCredential.zeroize();
            }
            LockscreenCredential lockscreenCredential2 = this.mCurrentCredential;
            if (lockscreenCredential2 != null) {
                lockscreenCredential2.zeroize();
            }
            LockscreenCredential lockscreenCredential3 = this.mFirstPassword;
            if (lockscreenCredential3 != null) {
                lockscreenCredential3.zeroize();
            }
            this.mCurrentPasswordEntry.setText("");
            if (!z && (redactionInterstitialIntent = getRedactionInterstitialIntent(getActivity())) != null) {
                startActivity(redactionInterstitialIntent);
            }
            getActivity().finish();
        }

        private void startCheckPassword(LockscreenCredential lockscreenCredential, boolean z) {
            int privacySpaceUserId = z ? ((UserManager) getActivity().getSystemService("user")).getPrivacySpaceUserId() : 0;
            LockPatternUtils lockPatternUtils = new LockPatternUtils(getActivity());
            this.mLockPatternUtils = lockPatternUtils;
            LockPatternChecker.checkCredential(lockPatternUtils, lockscreenCredential, privacySpaceUserId, new C1221xcfed0645(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$startCheckPassword$0(boolean z, int i) {
            onPasswordChecked(z);
        }

        private void onPasswordChecked(boolean z) {
            this.mCurrentEntryInputDisabler.setInputEnabled(true);
            if (z) {
                this.mPasswordRequirementAdapter.setRequirements(new String[]{getSamePasswordWarningMessage()});
                return;
            }
            this.mCurrentPasswordEntry.setText("");
            updateStage(Stage.NeedToConfirm);
        }

        private String getSamePasswordWarningMessage() {
            if (this.mIsAlphaMode) {
                if (this.mIsPrivacyUser) {
                    return getString(C1992R$string.lockpassword_privacy_password_same_as_main_space);
                }
                if (this.mIsCreatePrivacySpace || this.mIsPrivacySpaceCreated) {
                    return getString(C1992R$string.lockpassword_main_password_same_as_privacy_space);
                }
                return "";
            } else if (this.mIsPrivacyUser) {
                return getString(C1992R$string.lockpassword_privacy_pin_same_as_main_space);
            } else {
                if (this.mIsCreatePrivacySpace || this.mIsPrivacySpaceCreated) {
                    return getString(C1992R$string.lockpassword_main_pin_same_as_privacy_space);
                }
                return "";
            }
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
                if (ChooseLockPasswordFragment.this.getActivity() != null && message.what == 1) {
                    ChooseLockPasswordFragment.this.updateUi();
                }
            }
        }

        public void showMainSpaceLockTypeChangeWarningDialog() {
            PasswordChangeWarningDialog.newInstance(C1992R$string.f60xd63a9292, C1992R$string.f59xb8b649c1, C1992R$string.lock_settings_main_space_lock_type_change_warning_dialog_button).show(getChildFragmentManager(), "modify_lock_screen_of_main_space_dialog");
        }
    }

    public static class PasswordChangeWarningDialog extends InstrumentedDialogFragment {
        public int getMetricsCategory() {
            return 528;
        }

        public static PasswordChangeWarningDialog newInstance(int i, int i2, int i3) {
            PasswordChangeWarningDialog passwordChangeWarningDialog = new PasswordChangeWarningDialog();
            Bundle bundle = new Bundle();
            bundle.putInt("titleRes", i);
            bundle.putInt("messageRes", i2);
            bundle.putInt("positive_button", i3);
            passwordChangeWarningDialog.setArguments(bundle);
            return passwordChangeWarningDialog;
        }

        public void show(FragmentManager fragmentManager, String str) {
            if (fragmentManager.findFragmentByTag(str) == null) {
                super.show(fragmentManager, str);
            }
        }

        public Dialog onCreateDialog(Bundle bundle) {
            Bundle arguments = getArguments();
            return new AlertDialog.Builder(getActivity()).setTitle(arguments.getInt("titleRes")).setMessage(arguments.getInt("messageRes")).setPositiveButton(arguments.getInt("positive_button"), (DialogInterface.OnClickListener) new C1222x9d288661(this)).create();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateDialog$0(DialogInterface dialogInterface, int i) {
            dismiss();
        }
    }

    public static class SaveAndFinishWorker extends SaveChosenLockWorkerBase {
        private boolean isSixPinLock;
        private LockscreenCredential mChosenPassword;
        private LockscreenCredential mCurrentCredential;

        public void start(LockPatternUtils lockPatternUtils, boolean z, boolean z2, LockscreenCredential lockscreenCredential, LockscreenCredential lockscreenCredential2, int i, boolean z3) {
            prepare(lockPatternUtils, z, z2, i);
            this.mChosenPassword = lockscreenCredential;
            if (lockscreenCredential2 == null) {
                lockscreenCredential2 = LockscreenCredential.createNone();
            }
            this.mCurrentCredential = lockscreenCredential2;
            this.mUserId = i;
            this.isSixPinLock = z3;
            start();
        }

        /* access modifiers changed from: protected */
        public Pair<Boolean, Intent> saveAndVerifyInBackground() {
            boolean lockCredential = this.mUtils.setLockCredential(this.mChosenPassword, this.mCurrentCredential, this.mUserId);
            if (lockCredential) {
                unifyProfileCredentialIfRequested();
                this.mUtils.setSixBitForPin(this.isSixPinLock, this.mUserId);
            }
            Intent intent = null;
            if (lockCredential && this.mRequestGatekeeperPassword) {
                VerifyCredentialResponse verifyCredential = this.mUtils.verifyCredential(this.mChosenPassword, this.mUserId, 1);
                if (!verifyCredential.isMatched() || !verifyCredential.containsGatekeeperPasswordHandle()) {
                    Log.e("ChooseLockPassword", "critical: bad response or missing GK PW handle for known good password: " + verifyCredential.toString());
                }
                intent = new Intent();
                intent.putExtra("gk_pw_handle", verifyCredential.getGatekeeperPasswordHandle());
            }
            return Pair.create(Boolean.valueOf(lockCredential), intent);
        }
    }
}

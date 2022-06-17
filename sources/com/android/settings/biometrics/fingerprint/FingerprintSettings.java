package com.android.settings.biometrics.fingerprint;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintSensorPropertiesInternal;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImeAwareEditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.R$styleable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
import com.android.settings.C1981R$color;
import com.android.settings.C1983R$drawable;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.C1992R$string;
import com.android.settings.C1993R$style;
import com.android.settings.C1994R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SubSettings;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricUtils;
import com.android.settings.biometrics.fingerprint.FingerprintAuthenticateSidecar;
import com.android.settings.biometrics.fingerprint.FingerprintRemoveSidecar;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.utils.AnnotationSpan;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.TwoTargetPreference;
import com.motorola.settings.biometrics.fingerprint.FODAnimationPreferenceController;
import com.motorola.settings.biometrics.fingerprint.FingerprintTouchToUnlockSwitchPreferenceController;
import com.motorola.settings.biometrics.fingerprint.FingerprintUtils;
import com.motorola.settings.biometrics.fingerprint.HBMFODScreenProtectorWarningActivity;
import java.util.HashMap;
import java.util.List;

public class FingerprintSettings extends SubSettings {
    public Intent getIntent() {
        Intent intent = new Intent(super.getIntent());
        intent.putExtra(":settings:show_fragment", FingerprintSettingsFragment.class.getName());
        return intent;
    }

    /* access modifiers changed from: protected */
    public boolean isValidFragment(String str) {
        return FingerprintSettingsFragment.class.getName().equals(str);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (FingerprintUtils.isMotoFODEnabled(this)) {
            setRequestedOrientation(1);
        }
        setTitle(getText(C1992R$string.security_settings_fingerprint_preference_title));
        if (FingerprintUtils.needsDarkThemeEnforced(this)) {
            FingerprintUtils.setDarkThemeToolBar(this);
        }
    }

    public void setTheme(int i) {
        if (FingerprintUtils.needsDarkThemeEnforced(this)) {
            super.setTheme(C1993R$style.Theme_SettingsBase_Dark);
        } else {
            super.setTheme(i);
        }
    }

    public static class FingerprintSettingsFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, FingerprintPreference.OnDeleteClickListener {
        FingerprintAuthenticateSidecar.Listener mAuthenticateListener = new FingerprintAuthenticateSidecar.Listener() {
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult) {
                FingerprintSettingsFragment.this.mHandler.obtainMessage(1001, authenticationResult.getFingerprint().getBiometricId(), 0).sendToTarget();
            }

            public void onAuthenticationFailed() {
                FingerprintSettingsFragment.this.mHandler.obtainMessage(1002).sendToTarget();
            }

            public void onAuthenticationError(int i, CharSequence charSequence) {
                FingerprintSettingsFragment.this.mHandler.obtainMessage(1003, i, 0, charSequence).sendToTarget();
            }

            public void onAuthenticationHelp(int i, CharSequence charSequence) {
                FingerprintSettingsFragment.this.mHandler.obtainMessage(1004, i, 0, charSequence).sendToTarget();
            }
        };
        /* access modifiers changed from: private */
        public FingerprintAuthenticateSidecar mAuthenticateSidecar;
        private long mChallenge;
        private boolean mEnrollClicked;
        /* access modifiers changed from: private */
        public boolean mFODAnimationClicked;
        private final Runnable mFingerprintLockoutReset = new Runnable() {
            public void run() {
                boolean unused = FingerprintSettingsFragment.this.mInFingerprintLockout = false;
                FingerprintSettingsFragment.this.retryFingerprint();
            }
        };
        /* access modifiers changed from: private */
        public FingerprintManager mFingerprintManager;
        private HashMap<Integer, String> mFingerprintsRenaming;
        private CharSequence mFooterTitle;
        /* access modifiers changed from: private */
        public final Handler mHandler = new Handler() {
            public void handleMessage(Message message) {
                int i = message.what;
                if (i != 1000) {
                    if (i == 1001) {
                        FingerprintSettingsFragment.this.highlightFingerprintItem(message.arg1);
                        FingerprintSettingsFragment.this.retryFingerprint();
                    } else if (i == 1003) {
                        FingerprintSettingsFragment.this.handleError(message.arg1, (CharSequence) message.obj);
                    }
                } else if (!FingerprintSettingsFragment.this.mFingerprintManager.hasEnrolledFingerprints(FingerprintSettingsFragment.this.mUserId)) {
                    if (FingerprintSettingsFragment.this.mAuthenticateSidecar != null) {
                        FingerprintSettingsFragment.this.mAuthenticateSidecar.setListener((FingerprintAuthenticateSidecar.Listener) null);
                        FingerprintSettingsFragment.this.mAuthenticateSidecar.stopAuthentication();
                    }
                    if (FingerprintSettingsFragment.this.getActivity() != null) {
                        FingerprintSettingsFragment.this.getActivity().finish();
                    }
                } else {
                    FingerprintSettingsFragment.this.removeFingerprintPreference(message.arg1);
                    FingerprintSettingsFragment.this.updateAddPreference();
                    FingerprintSettingsFragment.this.retryFingerprint();
                }
            }
        };
        private Drawable mHighlightDrawable;
        /* access modifiers changed from: private */
        public boolean mInFingerprintLockout;
        private boolean mIsMotoFODEnroll;
        private boolean mLaunchedConfirm;
        FingerprintRemoveSidecar.Listener mRemovalListener = new FingerprintRemoveSidecar.Listener() {
            public void onRemovalSucceeded(Fingerprint fingerprint) {
                FingerprintSettingsFragment.this.mHandler.obtainMessage(1000, fingerprint.getBiometricId(), 0).sendToTarget();
                updateDialog();
            }

            public void onRemovalError(Fingerprint fingerprint, int i, CharSequence charSequence) {
                FragmentActivity activity = FingerprintSettingsFragment.this.getActivity();
                if (activity != null) {
                    Toast.makeText(activity, charSequence, 0);
                }
                updateDialog();
            }

            private void updateDialog() {
                RenameDialog renameDialog = (RenameDialog) FingerprintSettingsFragment.this.getFragmentManager().findFragmentByTag(RenameDialog.class.getName());
                if (renameDialog != null) {
                    renameDialog.enableDelete();
                }
            }
        };
        private FingerprintRemoveSidecar mRemovalSidecar;
        private List<FingerprintSensorPropertiesInternal> mSensorProperties;
        private byte[] mToken;
        /* access modifiers changed from: private */
        public int mUserId;

        public int getMetricsCategory() {
            return 49;
        }

        public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
            super.onCreateOptionsMenu(menu, menuInflater);
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).getIcon().setColorFilter(getResources().getColor(C1981R$color.fod_fingerprint_title_color), PorterDuff.Mode.SRC_ATOP);
            }
        }

        /* access modifiers changed from: protected */
        public void handleError(int i, CharSequence charSequence) {
            FragmentActivity activity;
            if (i != 5) {
                if (i == 7) {
                    this.mInFingerprintLockout = true;
                    if (!this.mHandler.hasCallbacks(this.mFingerprintLockoutReset)) {
                        this.mHandler.postDelayed(this.mFingerprintLockoutReset, 30000);
                    }
                } else if (i == 9) {
                    this.mInFingerprintLockout = true;
                } else if (i == 10) {
                    return;
                }
                if (this.mInFingerprintLockout && (activity = getActivity()) != null) {
                    Toast.makeText(activity, charSequence, 0).show();
                }
                retryFingerprint();
            }
        }

        /* access modifiers changed from: private */
        public void retryFingerprint() {
            if (!isUdfps() && !this.mRemovalSidecar.inProgress() && this.mFingerprintManager.getEnrolledFingerprints(this.mUserId).size() != 0 && !this.mLaunchedConfirm && !this.mInFingerprintLockout) {
                this.mAuthenticateSidecar.startAuthentication(this.mUserId);
                this.mAuthenticateSidecar.setListener(this.mAuthenticateListener);
            }
        }

        public void onCreate(Bundle bundle) {
            int i;
            super.onCreate(bundle);
            FragmentActivity activity = getActivity();
            this.mIsMotoFODEnroll = FingerprintUtils.isMotoFODEnabled(activity);
            FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(activity);
            this.mFingerprintManager = fingerprintManagerOrNull;
            this.mSensorProperties = fingerprintManagerOrNull.getSensorPropertiesInternal();
            this.mToken = getIntent().getByteArrayExtra("hw_auth_token");
            this.mChallenge = activity.getIntent().getLongExtra("challenge", -1);
            FingerprintAuthenticateSidecar fingerprintAuthenticateSidecar = (FingerprintAuthenticateSidecar) getFragmentManager().findFragmentByTag("authenticate_sidecar");
            this.mAuthenticateSidecar = fingerprintAuthenticateSidecar;
            if (fingerprintAuthenticateSidecar == null) {
                this.mAuthenticateSidecar = new FingerprintAuthenticateSidecar();
                getFragmentManager().beginTransaction().add((Fragment) this.mAuthenticateSidecar, "authenticate_sidecar").commit();
            }
            this.mAuthenticateSidecar.setFingerprintManager(this.mFingerprintManager);
            FingerprintRemoveSidecar fingerprintRemoveSidecar = (FingerprintRemoveSidecar) getFragmentManager().findFragmentByTag("removal_sidecar");
            this.mRemovalSidecar = fingerprintRemoveSidecar;
            if (fingerprintRemoveSidecar == null) {
                this.mRemovalSidecar = new FingerprintRemoveSidecar();
                getFragmentManager().beginTransaction().add((Fragment) this.mRemovalSidecar, "removal_sidecar").commit();
            }
            this.mRemovalSidecar.setFingerprintManager(this.mFingerprintManager);
            this.mRemovalSidecar.setListener(this.mRemovalListener);
            RenameDialog renameDialog = (RenameDialog) getFragmentManager().findFragmentByTag(RenameDialog.class.getName());
            if (renameDialog != null) {
                renameDialog.setDeleteInProgress(this.mRemovalSidecar.inProgress());
            }
            this.mFingerprintsRenaming = new HashMap<>();
            if (bundle != null) {
                this.mFingerprintsRenaming = (HashMap) bundle.getSerializable("mFingerprintsRenaming");
                this.mToken = bundle.getByteArray("hw_auth_token");
                this.mLaunchedConfirm = bundle.getBoolean("launched_confirm", false);
            }
            this.mUserId = getActivity().getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
            if (this.mToken == null && !this.mLaunchedConfirm) {
                this.mLaunchedConfirm = true;
                launchChooseOrConfirmLock();
            }
            RestrictedLockUtils.EnforcedAdmin checkIfKeyguardFeaturesDisabled = RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(activity, 32, this.mUserId);
            AnnotationSpan.LinkInfo linkInfo = new AnnotationSpan.LinkInfo("admin_details", new C0786xd2803eaa(activity, checkIfKeyguardFeaturesDisabled));
            AnnotationSpan.LinkInfo linkInfo2 = new AnnotationSpan.LinkInfo(activity, "url", HelpUtils.getHelpIntent(activity, getString(C1992R$string.help_url_fingerprint_learn_more), activity.getClass().getName()));
            if (checkIfKeyguardFeaturesDisabled != null) {
                i = C1992R$string.f70x60fec2a1;
            } else {
                i = C1992R$string.security_settings_fingerprint_enroll_disclaimer;
            }
            CharSequence text = getText(i);
            if (linkInfo2.isActionable()) {
                this.mFooterTitle = AnnotationSpan.linkify(text, linkInfo2, linkInfo);
            } else {
                this.mFooterTitle = AnnotationSpan.textWithoutLink(text);
            }
            getFragmentManager().setFragmentResultListener("rename_dialog", this, new C0787xd2803eab(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreate$1(String str, Bundle bundle) {
            retryFingerprint();
        }

        private boolean isUdfps() {
            for (FingerprintSensorPropertiesInternal isAnyUdfpsType : this.mSensorProperties) {
                if (isAnyUdfpsType.isAnyUdfpsType()) {
                    return !this.mIsMotoFODEnroll;
                }
            }
            return false;
        }

        /* access modifiers changed from: protected */
        public void removeFingerprintPreference(int i) {
            String genKey = genKey(i);
            Preference findPreference = findPreference(genKey);
            if (findPreference == null) {
                Log.w("FingerprintSettings", "Can't find preference to remove: " + genKey);
            } else if (!getPreferenceScreen().removePreference(findPreference)) {
                Log.w("FingerprintSettings", "Failed to remove preference with key " + genKey);
            }
        }

        private PreferenceScreen createPreferenceHierarchy() {
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            if (preferenceScreen != null) {
                preferenceScreen.removeAll();
            }
            addPreferencesFromResource(C1994R$xml.security_settings_fingerprint);
            PreferenceScreen preferenceScreen2 = getPreferenceScreen();
            addFingerprintItemPreferences(preferenceScreen2);
            addTouchToUnlockSwitchPreference(preferenceScreen2);
            addFODAnimationStylesPreference(preferenceScreen2);
            setPreferenceScreen(preferenceScreen2);
            return preferenceScreen2;
        }

        private void addFODAnimationStylesPreference(PreferenceScreen preferenceScreen) {
            if (FingerprintUtils.isMotoFODEnabled(preferenceScreen.getContext())) {
                Preference preference = new Preference(preferenceScreen.getContext());
                preference.setKey(FODAnimationPreferenceController.KEY);
                preferenceScreen.addPreference(preference);
                FODAnimationPreferenceController fODAnimationPreferenceController = new FODAnimationPreferenceController(preferenceScreen.getContext());
                fODAnimationPreferenceController.displayPreference(preferenceScreen);
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference preference) {
                        boolean unused = FingerprintSettingsFragment.this.mFODAnimationClicked = true;
                        return false;
                    }
                });
                fODAnimationPreferenceController.updateState(preference);
            }
        }

        private void addFingerprintItemPreferences(PreferenceGroup preferenceGroup) {
            preferenceGroup.removeAll();
            List enrolledFingerprints = this.mFingerprintManager.getEnrolledFingerprints(this.mUserId);
            int size = enrolledFingerprints.size();
            for (int i = 0; i < size; i++) {
                Fingerprint fingerprint = (Fingerprint) enrolledFingerprints.get(i);
                FingerprintPreference fingerprintPreference = new FingerprintPreference(preferenceGroup.getContext(), this);
                fingerprintPreference.setKey(genKey(fingerprint.getBiometricId()));
                fingerprintPreference.setTitle(fingerprint.getName());
                fingerprintPreference.setFingerprint(fingerprint);
                fingerprintPreference.setPersistent(false);
                fingerprintPreference.setIcon(C1983R$drawable.ic_fingerprint_24dp);
                if (this.mRemovalSidecar.isRemovingFingerprint(fingerprint.getBiometricId())) {
                    fingerprintPreference.setEnabled(false);
                }
                if (this.mFingerprintsRenaming.containsKey(Integer.valueOf(fingerprint.getBiometricId()))) {
                    fingerprintPreference.setTitle((CharSequence) this.mFingerprintsRenaming.get(Integer.valueOf(fingerprint.getBiometricId())));
                }
                preferenceGroup.addPreference(fingerprintPreference);
                fingerprintPreference.setOnPreferenceChangeListener(this);
            }
            Preference preference = new Preference(preferenceGroup.getContext());
            preference.setKey("key_fingerprint_add");
            preference.setTitle(C1992R$string.fingerprint_add_title);
            preference.setIcon(C1983R$drawable.ic_add_24dp);
            preferenceGroup.addPreference(preference);
            preference.setOnPreferenceChangeListener(this);
            updateAddPreference();
            createFooterPreference(preferenceGroup);
        }

        private void addTouchToUnlockSwitchPreference(PreferenceGroup preferenceGroup) {
            if (this.mFingerprintManager.getEnrolledFingerprints(this.mUserId).size() > 0 && FingerprintUtils.isSideFingerPrint(preferenceGroup.getContext())) {
                new SwitchPreference(preferenceGroup.getContext()).setKey(FingerprintTouchToUnlockSwitchPreferenceController.KEY);
                new FingerprintTouchToUnlockSwitchPreferenceController(preferenceGroup.getContext()).displayPreference(preferenceGroup);
            }
        }

        /* access modifiers changed from: private */
        public void updateAddPreference() {
            if (getActivity() != null) {
                Preference findPreference = findPreference("key_fingerprint_add");
                int integer = getContext().getResources().getInteger(17694823);
                boolean z = true;
                boolean z2 = this.mFingerprintManager.getEnrolledFingerprints(this.mUserId).size() >= integer;
                boolean inProgress = this.mRemovalSidecar.inProgress();
                findPreference.setSummary((CharSequence) z2 ? getContext().getString(C1992R$string.fingerprint_add_max, new Object[]{Integer.valueOf(integer)}) : "");
                if (z2 || inProgress || this.mToken == null) {
                    z = false;
                }
                findPreference.setEnabled(z);
            }
        }

        private void createFooterPreference(PreferenceGroup preferenceGroup) {
            FragmentActivity activity = getActivity();
            if (activity != null) {
                preferenceGroup.addPreference(new FooterPreference.Builder(activity).setTitle(this.mFooterTitle).build());
            }
        }

        private static String genKey(int i) {
            return "key_fingerprint_item_" + i;
        }

        public void onResume() {
            super.onResume();
            this.mInFingerprintLockout = false;
            updatePreferences();
            FingerprintRemoveSidecar fingerprintRemoveSidecar = this.mRemovalSidecar;
            if (fingerprintRemoveSidecar != null) {
                fingerprintRemoveSidecar.setListener(this.mRemovalListener);
            }
            if (!this.mFingerprintManager.hasEnrolledFingerprints(this.mUserId)) {
                getActivity().finish();
            }
            this.mFODAnimationClicked = false;
        }

        private void updatePreferences() {
            createPreferenceHierarchy();
            retryFingerprint();
        }

        public void onPause() {
            super.onPause();
            FingerprintRemoveSidecar fingerprintRemoveSidecar = this.mRemovalSidecar;
            if (fingerprintRemoveSidecar != null) {
                fingerprintRemoveSidecar.setListener((FingerprintRemoveSidecar.Listener) null);
            }
            FingerprintAuthenticateSidecar fingerprintAuthenticateSidecar = this.mAuthenticateSidecar;
            if (fingerprintAuthenticateSidecar != null) {
                fingerprintAuthenticateSidecar.setListener((FingerprintAuthenticateSidecar.Listener) null);
                this.mAuthenticateSidecar.stopAuthentication();
            }
        }

        public void onStop() {
            super.onStop();
            if (!getActivity().isChangingConfigurations() && !this.mLaunchedConfirm && !this.mEnrollClicked && !this.mFODAnimationClicked) {
                getActivity().finish();
            }
        }

        public void onSaveInstanceState(Bundle bundle) {
            bundle.putByteArray("hw_auth_token", this.mToken);
            bundle.putBoolean("launched_confirm", this.mLaunchedConfirm);
            bundle.putSerializable("mFingerprintsRenaming", this.mFingerprintsRenaming);
        }

        public boolean onPreferenceTreeClick(Preference preference) {
            if ("key_fingerprint_add".equals(preference.getKey())) {
                this.mEnrollClicked = true;
                if (FingerprintUtils.isMotoFODEnabled(getPrefContext())) {
                    launchScreenProtectorWarning();
                } else {
                    launchFingerprintEnroll();
                }
            } else if (preference instanceof FingerprintPreference) {
                showRenameDialog(((FingerprintPreference) preference).getFingerprint());
            }
            return super.onPreferenceTreeClick(preference);
        }

        private void launchScreenProtectorWarning() {
            Intent intent = new Intent();
            intent.setClassName("com.android.settings", HBMFODScreenProtectorWarningActivity.class.getName());
            intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
            intent.putExtra("hw_auth_token", this.mToken);
            startActivityForResult(intent, R$styleable.Constraint_layout_goneMarginTop);
        }

        private void launchFingerprintEnroll() {
            Intent intent = new Intent();
            intent.setClassName("com.android.settings", FingerprintEnrollEnrolling.class.getName());
            intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
            intent.putExtra("hw_auth_token", this.mToken);
            intent.putExtra("from_fingerprint_settings", true);
            startActivityForResult(intent, 10);
        }

        public void onDeleteClick(FingerprintPreference fingerprintPreference) {
            boolean z = true;
            if (this.mFingerprintManager.getEnrolledFingerprints(this.mUserId).size() <= 1) {
                z = false;
            }
            Fingerprint fingerprint = fingerprintPreference.getFingerprint();
            if (!z) {
                ConfirmLastDeleteDialog confirmLastDeleteDialog = new ConfirmLastDeleteDialog();
                boolean isManagedProfile = UserManager.get(getContext()).isManagedProfile(this.mUserId);
                Bundle bundle = new Bundle();
                bundle.putParcelable("fingerprint", fingerprint);
                bundle.putBoolean("isProfileChallengeUser", isManagedProfile);
                confirmLastDeleteDialog.setArguments(bundle);
                confirmLastDeleteDialog.setTargetFragment(this, 0);
                confirmLastDeleteDialog.show(getFragmentManager(), ConfirmLastDeleteDialog.class.getName());
            } else if (this.mRemovalSidecar.inProgress()) {
                Log.d("FingerprintSettings", "Fingerprint delete in progress, skipping");
            } else {
                DeleteFingerprintDialog.newInstance(fingerprint, this).show(getFragmentManager(), DeleteFingerprintDialog.class.getName());
            }
        }

        private void showRenameDialog(Fingerprint fingerprint) {
            RenameDialog renameDialog = new RenameDialog();
            Bundle bundle = new Bundle();
            if (this.mFingerprintsRenaming.containsKey(Integer.valueOf(fingerprint.getBiometricId()))) {
                bundle.putParcelable("fingerprint", new Fingerprint(this.mFingerprintsRenaming.get(Integer.valueOf(fingerprint.getBiometricId())), fingerprint.getGroupId(), fingerprint.getBiometricId(), fingerprint.getDeviceId()));
            } else {
                bundle.putParcelable("fingerprint", fingerprint);
            }
            renameDialog.setDeleteInProgress(this.mRemovalSidecar.inProgress());
            renameDialog.setArguments(bundle);
            renameDialog.setTargetFragment(this, 0);
            renameDialog.show(getFragmentManager(), RenameDialog.class.getName());
            this.mAuthenticateSidecar.stopAuthentication();
        }

        public boolean onPreferenceChange(Preference preference, Object obj) {
            String key = preference.getKey();
            if ("fingerprint_enable_keyguard_toggle".equals(key)) {
                return true;
            }
            Log.v("FingerprintSettings", "Unknown key:" + key);
            return true;
        }

        public int getHelpResource() {
            return C1992R$string.help_url_fingerprint;
        }

        public void onActivityResult(int i, int i2, Intent intent) {
            super.onActivityResult(i, i2, intent);
            if (i == 101 || i == 102) {
                this.mLaunchedConfirm = false;
                if (i2 != 1 && i2 != -1) {
                    Log.d("FingerprintSettings", "Password not confirmed");
                    finish();
                } else if (intent == null || !BiometricUtils.containsGatekeeperPasswordHandle(intent)) {
                    Log.d("FingerprintSettings", "Data null or GK PW missing");
                    finish();
                } else {
                    this.mFingerprintManager.generateChallenge(this.mUserId, new C0785xd2803ea9(this, intent));
                }
            } else if (i == 103) {
                if (i2 == 1) {
                    launchFingerprintEnroll();
                }
            } else if (i == 10) {
                this.mEnrollClicked = false;
                if (i2 == 3) {
                    FragmentActivity activity = getActivity();
                    activity.setResult(i2);
                    activity.finish();
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onActivityResult$2(Intent intent, int i, int i2, long j) {
            this.mToken = BiometricUtils.requestGatekeeperHat((Context) getActivity(), intent, this.mUserId, j);
            this.mChallenge = j;
            BiometricUtils.removeGatekeeperPasswordHandle((Context) getActivity(), intent);
            updateAddPreference();
        }

        public void onDestroy() {
            super.onDestroy();
            if (getActivity().isFinishing()) {
                this.mFingerprintManager.revokeChallenge(this.mUserId, this.mChallenge);
            }
            getFragmentManager().clearFragmentResultListener("rename_dialog");
        }

        private Drawable getHighlightDrawable() {
            FragmentActivity activity;
            if (this.mHighlightDrawable == null && (activity = getActivity()) != null) {
                this.mHighlightDrawable = activity.getDrawable(C1983R$drawable.preference_highlight);
            }
            return this.mHighlightDrawable;
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:3:0x0012, code lost:
            r4 = r4.getView();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void highlightFingerprintItem(int r4) {
            /*
                r3 = this;
                java.lang.String r4 = genKey(r4)
                androidx.preference.Preference r4 = r3.findPreference(r4)
                com.android.settings.biometrics.fingerprint.FingerprintSettings$FingerprintPreference r4 = (com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintPreference) r4
                android.graphics.drawable.Drawable r0 = r3.getHighlightDrawable()
                if (r0 == 0) goto L_0x0041
                if (r4 == 0) goto L_0x0041
                android.view.View r4 = r4.getView()
                if (r4 != 0) goto L_0x0019
                return
            L_0x0019:
                int r1 = r4.getWidth()
                int r1 = r1 / 2
                int r2 = r4.getHeight()
                int r2 = r2 / 2
                float r1 = (float) r1
                float r2 = (float) r2
                r0.setHotspot(r1, r2)
                r4.setBackground(r0)
                r0 = 1
                r4.setPressed(r0)
                r0 = 0
                r4.setPressed(r0)
                android.os.Handler r0 = r3.mHandler
                com.android.settings.biometrics.fingerprint.FingerprintSettings$FingerprintSettingsFragment$5 r1 = new com.android.settings.biometrics.fingerprint.FingerprintSettings$FingerprintSettingsFragment$5
                r1.<init>(r4)
                r3 = 500(0x1f4, double:2.47E-321)
                r0.postDelayed(r1, r3)
            L_0x0041:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.biometrics.fingerprint.FingerprintSettings.FingerprintSettingsFragment.highlightFingerprintItem(int):void");
        }

        private void launchChooseOrConfirmLock() {
            Intent intent = new Intent();
            if (!new ChooseLockSettingsHelper.Builder(getActivity(), this).setRequestCode(R$styleable.Constraint_layout_goneMarginRight).setTitle(getString(C1992R$string.security_settings_fingerprint_preference_title)).setRequestGatekeeperPasswordHandle(true).setUserId(this.mUserId).setForegroundOnly(true).setReturnCredentials(true).show()) {
                intent.setClassName("com.android.settings", ChooseLockGeneric.class.getName());
                intent.putExtra("hide_insecure_options", true);
                intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
                intent.putExtra("request_gk_pw_handle", true);
                intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
                startActivityForResult(intent, 102);
            }
        }

        /* access modifiers changed from: package-private */
        public void deleteFingerPrint(Fingerprint fingerprint) {
            this.mRemovalSidecar.startRemove(fingerprint, this.mUserId);
            Preference findPreference = findPreference(genKey(fingerprint.getBiometricId()));
            if (findPreference != null) {
                findPreference.setEnabled(false);
            }
            updateAddPreference();
        }

        /* access modifiers changed from: private */
        public void renameFingerPrint(int i, String str) {
            this.mFingerprintManager.rename(i, this.mUserId, str);
            if (!TextUtils.isEmpty(str)) {
                this.mFingerprintsRenaming.put(Integer.valueOf(i), str);
            }
            updatePreferences();
        }

        public static class DeleteFingerprintDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
            private AlertDialog mAlertDialog;
            private Fingerprint mFp;

            public int getMetricsCategory() {
                return 570;
            }

            public static DeleteFingerprintDialog newInstance(Fingerprint fingerprint, FingerprintSettingsFragment fingerprintSettingsFragment) {
                DeleteFingerprintDialog deleteFingerprintDialog = new DeleteFingerprintDialog();
                Bundle bundle = new Bundle();
                bundle.putParcelable("fingerprint", fingerprint);
                deleteFingerprintDialog.setArguments(bundle);
                deleteFingerprintDialog.setTargetFragment(fingerprintSettingsFragment, 0);
                return deleteFingerprintDialog;
            }

            public Dialog onCreateDialog(Bundle bundle) {
                Fingerprint parcelable = getArguments().getParcelable("fingerprint");
                this.mFp = parcelable;
                AlertDialog create = new AlertDialog.Builder(getActivity()).setTitle((CharSequence) getString(C1992R$string.fingerprint_delete_title, parcelable.getName())).setMessage(C1992R$string.fingerprint_delete_message).setPositiveButton(C1992R$string.security_settings_fingerprint_enroll_dialog_delete, (DialogInterface.OnClickListener) this).setNegativeButton(C1992R$string.cancel, (DialogInterface.OnClickListener) null).create();
                this.mAlertDialog = create;
                return create;
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    int biometricId = this.mFp.getBiometricId();
                    Log.v("FingerprintSettings", "Removing fpId=" + biometricId);
                    this.mMetricsFeatureProvider.action(getContext(), 253, biometricId);
                    ((FingerprintSettingsFragment) getTargetFragment()).deleteFingerPrint(this.mFp);
                }
            }
        }

        /* access modifiers changed from: private */
        public static InputFilter[] getFilters() {
            return new InputFilter[]{new InputFilter() {
                public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                    while (i < i2) {
                        if (charSequence.charAt(i) < ' ') {
                            return "";
                        }
                        i++;
                    }
                    return null;
                }
            }};
        }

        public static class RenameDialog extends InstrumentedDialogFragment {
            /* access modifiers changed from: private */
            public AlertDialog mAlertDialog;
            /* access modifiers changed from: private */
            public boolean mDeleteInProgress;
            /* access modifiers changed from: private */
            public ImeAwareEditText mDialogTextField;
            /* access modifiers changed from: private */
            public Fingerprint mFp;

            public int getMetricsCategory() {
                return 570;
            }

            /* access modifiers changed from: private */
            public void setOnDismissResult() {
                getFragmentManager().setFragmentResult("rename_dialog", new Bundle());
            }

            public void setDeleteInProgress(boolean z) {
                this.mDeleteInProgress = z;
            }

            public void onCancel(DialogInterface dialogInterface) {
                super.onCancel(dialogInterface);
                setOnDismissResult();
            }

            public Dialog onCreateDialog(Bundle bundle) {
                final int i;
                final String str;
                this.mFp = getArguments().getParcelable("fingerprint");
                final int i2 = -1;
                if (bundle != null) {
                    str = bundle.getString("fingerName");
                    int i3 = bundle.getInt("startSelection", -1);
                    i = bundle.getInt("endSelection", -1);
                    i2 = i3;
                } else {
                    str = null;
                    i = -1;
                }
                AlertDialog create = new AlertDialog.Builder(getActivity()).setView(C1987R$layout.fingerprint_rename_dialog).setPositiveButton(C1992R$string.security_settings_fingerprint_enroll_dialog_ok, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String obj = RenameDialog.this.mDialogTextField.getText().toString();
                        CharSequence name = RenameDialog.this.mFp.getName();
                        if (!TextUtils.equals(obj, name)) {
                            Log.d("FingerprintSettings", "rename " + name + " to " + obj);
                            RenameDialog.this.mMetricsFeatureProvider.action(RenameDialog.this.getContext(), 254, RenameDialog.this.mFp.getBiometricId());
                            ((FingerprintSettingsFragment) RenameDialog.this.getTargetFragment()).renameFingerPrint(RenameDialog.this.mFp.getBiometricId(), obj);
                        }
                        RenameDialog.this.setOnDismissResult();
                        dialogInterface.dismiss();
                    }
                }).create();
                this.mAlertDialog = create;
                create.setOnShowListener(new DialogInterface.OnShowListener() {
                    public void onShow(DialogInterface dialogInterface) {
                        RenameDialog renameDialog = RenameDialog.this;
                        ImeAwareEditText unused = renameDialog.mDialogTextField = renameDialog.mAlertDialog.findViewById(C1985R$id.fingerprint_rename_field);
                        CharSequence charSequence = str;
                        if (charSequence == null) {
                            charSequence = RenameDialog.this.mFp.getName();
                        }
                        RenameDialog.this.mDialogTextField.setText(charSequence);
                        RenameDialog.this.mDialogTextField.setFilters(FingerprintSettingsFragment.getFilters());
                        if (i2 == -1 || i == -1) {
                            RenameDialog.this.mDialogTextField.selectAll();
                        } else {
                            RenameDialog.this.mDialogTextField.setSelection(i2, i);
                        }
                        if (RenameDialog.this.mDeleteInProgress) {
                            RenameDialog.this.mAlertDialog.getButton(-2).setEnabled(false);
                        }
                        RenameDialog.this.mDialogTextField.requestFocus();
                        RenameDialog.this.mDialogTextField.scheduleShowSoftInput();
                    }
                });
                return this.mAlertDialog;
            }

            public void enableDelete() {
                this.mDeleteInProgress = false;
                AlertDialog alertDialog = this.mAlertDialog;
                if (alertDialog != null) {
                    alertDialog.getButton(-2).setEnabled(true);
                }
            }

            public void onSaveInstanceState(Bundle bundle) {
                super.onSaveInstanceState(bundle);
                ImeAwareEditText imeAwareEditText = this.mDialogTextField;
                if (imeAwareEditText != null) {
                    bundle.putString("fingerName", imeAwareEditText.getText().toString());
                    bundle.putInt("startSelection", this.mDialogTextField.getSelectionStart());
                    bundle.putInt("endSelection", this.mDialogTextField.getSelectionEnd());
                }
            }
        }

        public static class ConfirmLastDeleteDialog extends InstrumentedDialogFragment {
            /* access modifiers changed from: private */
            public Fingerprint mFp;

            public int getMetricsCategory() {
                return 571;
            }

            public Dialog onCreateDialog(Bundle bundle) {
                int i;
                this.mFp = getArguments().getParcelable("fingerprint");
                boolean z = getArguments().getBoolean("isProfileChallengeUser");
                AlertDialog.Builder title = new AlertDialog.Builder(getActivity()).setTitle(C1992R$string.fingerprint_last_delete_title);
                if (z) {
                    i = C1992R$string.fingerprint_last_delete_message_profile_challenge;
                } else {
                    i = C1992R$string.fingerprint_last_delete_message;
                }
                return title.setMessage(i).setPositiveButton(C1992R$string.fingerprint_last_delete_confirm, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((FingerprintSettingsFragment) ConfirmLastDeleteDialog.this.getTargetFragment()).deleteFingerPrint(ConfirmLastDeleteDialog.this.mFp);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(C1992R$string.cancel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
            }
        }
    }

    public static class FingerprintPreference extends TwoTargetPreference {
        private View mDeleteView;
        private Fingerprint mFingerprint;
        /* access modifiers changed from: private */
        public final OnDeleteClickListener mOnDeleteClickListener;
        private View mView;

        public interface OnDeleteClickListener {
            void onDeleteClick(FingerprintPreference fingerprintPreference);
        }

        public FingerprintPreference(Context context, OnDeleteClickListener onDeleteClickListener) {
            super(context);
            this.mOnDeleteClickListener = onDeleteClickListener;
        }

        public View getView() {
            return this.mView;
        }

        public void setFingerprint(Fingerprint fingerprint) {
            this.mFingerprint = fingerprint;
        }

        public Fingerprint getFingerprint() {
            return this.mFingerprint;
        }

        /* access modifiers changed from: protected */
        public int getSecondTargetResId() {
            return C1987R$layout.preference_widget_delete;
        }

        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            super.onBindViewHolder(preferenceViewHolder);
            View view = preferenceViewHolder.itemView;
            this.mView = view;
            View findViewById = view.findViewById(C1985R$id.delete_button);
            this.mDeleteView = findViewById;
            findViewById.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (FingerprintPreference.this.mOnDeleteClickListener != null) {
                        FingerprintPreference.this.mOnDeleteClickListener.onDeleteClick(FingerprintPreference.this);
                    }
                }
            });
        }
    }
}

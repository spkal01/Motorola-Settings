package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.Intent;
import android.hardware.face.FaceManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricUtils;
import com.android.settings.biometrics.face.FaceSettingsEnrollButtonPreferenceController;
import com.android.settings.biometrics.face.FaceSettingsRemoveButtonPreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.motorola.settings.biometrics.face.FaceSettingsLTVPreferenceController;
import com.motorola.settings.biometrics.face.FaceSettingsRedoPreferenceController;
import com.motorola.settings.biometrics.face.FaceUtils;
import com.motorola.settings.biometrics.face.MotoFaceLicensesMenuController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FaceSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.security_settings_face) {
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            if (FaceSettings.isFaceHardwareDetected(context)) {
                return FaceSettings.buildPreferenceControllers(context, (Lifecycle) null);
            }
            return null;
        }

        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            if (FaceSettings.isFaceHardwareDetected(context)) {
                return hasEnrolledBiometrics(context);
            }
            return false;
        }

        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            boolean isFaceHardwareDetected = FaceSettings.isFaceHardwareDetected(context);
            Log.d("FaceSettings", "Get non indexable keys. isFaceHardwareDetected: " + isFaceHardwareDetected + ", size:" + nonIndexableKeys.size());
            if (isFaceHardwareDetected) {
                nonIndexableKeys.add(hasEnrolledBiometrics(context) ? "security_settings_face_enroll_faces_container" : "security_settings_face_delete_faces_container");
            }
            if (!isAttentionSupported(context)) {
                nonIndexableKeys.add(FaceSettingsAttentionPreferenceController.KEY);
            }
            if (FaceUtils.isMotoFaceUnlock()) {
                nonIndexableKeys.add("security_settings_face_unlock_category");
            }
            return nonIndexableKeys;
        }

        private boolean isAttentionSupported(Context context) {
            FaceFeatureProvider faceFeatureProvider = FeatureFactory.getFactory(context).getFaceFeatureProvider();
            if (faceFeatureProvider != null) {
                return faceFeatureProvider.isAttentionSupported(context);
            }
            return false;
        }

        private boolean hasEnrolledBiometrics(Context context) {
            FaceManager faceManagerOrNull = Utils.getFaceManagerOrNull(context);
            if (faceManagerOrNull != null) {
                return faceManagerOrNull.hasEnrolledTemplates(UserHandle.myUserId());
            }
            return false;
        }
    };
    private FaceSettingsAttentionPreferenceController mAttentionController;
    private long mChallenge;
    private boolean mConfirmingPassword;
    private List<AbstractPreferenceController> mControllers;
    private Preference mEnrollButton;
    private FaceSettingsEnrollButtonPreferenceController mEnrollController;
    private final FaceSettingsEnrollButtonPreferenceController.Listener mEnrollListener = new FaceSettings$$ExternalSyntheticLambda1(this);
    private FaceFeatureProvider mFaceFeatureProvider;
    private FaceManager mFaceManager;
    private FaceSettingsLockscreenBypassPreferenceController mLockscreenController;
    private FaceSettingsRedoPreferenceController mRedoController;
    private final FaceSettingsRemoveButtonPreferenceController.Listener mRemovalListener = new FaceSettings$$ExternalSyntheticLambda2(this);
    private Preference mRemoveButton;
    private FaceSettingsRemoveButtonPreferenceController mRemoveController;
    private int mSensorId;
    private List<Preference> mTogglePreferences;
    private byte[] mToken;
    private int mUserId;
    private UserManager mUserManager;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "FaceSettings";
    }

    public int getMetricsCategory() {
        return 1511;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(Intent intent) {
        startActivityForResult(intent, 5);
    }

    public static boolean isFaceHardwareDetected(Context context) {
        boolean z;
        FaceManager faceManagerOrNull = Utils.getFaceManagerOrNull(context);
        if (faceManagerOrNull == null) {
            Log.d("FaceSettings", "FaceManager is null");
            z = false;
        } else {
            z = faceManagerOrNull.isHardwareDetected();
            Log.d("FaceSettings", "FaceManager is not null. Hardware detected: " + z);
        }
        if (faceManagerOrNull == null || !z) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.security_settings_face;
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putByteArray("hw_auth_token", this.mToken);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context prefContext = getPrefContext();
        if (!isFaceHardwareDetected(prefContext)) {
            Log.w("FaceSettings", "no faceManager, finish this");
            finish();
            return;
        }
        this.mUserManager = (UserManager) prefContext.getSystemService(UserManager.class);
        this.mFaceManager = (FaceManager) prefContext.getSystemService(FaceManager.class);
        this.mToken = getIntent().getByteArrayExtra("hw_auth_token");
        this.mSensorId = getIntent().getIntExtra("sensor_id", -1);
        this.mChallenge = getIntent().getLongExtra("challenge", 0);
        this.mUserId = getActivity().getIntent().getIntExtra("android.intent.extra.USER_ID", UserHandle.myUserId());
        this.mFaceFeatureProvider = FeatureFactory.getFactory(getContext()).getFaceFeatureProvider();
        if (this.mUserManager.getUserInfo(this.mUserId).isManagedProfile()) {
            getActivity().setTitle(getActivity().getResources().getString(C1992R$string.security_settings_face_profile_preference_title));
        }
        FaceSettingsLockscreenBypassPreferenceController faceSettingsLockscreenBypassPreferenceController = (FaceSettingsLockscreenBypassPreferenceController) use(FaceSettingsLockscreenBypassPreferenceController.class);
        this.mLockscreenController = faceSettingsLockscreenBypassPreferenceController;
        if (faceSettingsLockscreenBypassPreferenceController != null) {
            faceSettingsLockscreenBypassPreferenceController.setUserId(this.mUserId);
        }
        this.mTogglePreferences = new ArrayList(Arrays.asList(new Preference[]{findPreference("security_settings_face_keyguard"), findPreference("security_settings_face_app"), findPreference(FaceSettingsAttentionPreferenceController.KEY), findPreference("security_settings_face_require_confirmation"), findPreference(FaceSettingsLTVPreferenceController.KEY)}));
        this.mRemoveButton = findPreference("security_settings_face_delete_faces_container");
        this.mEnrollButton = findPreference("security_settings_face_enroll_faces_container");
        for (AbstractPreferenceController next : this.mControllers) {
            if (next instanceof FaceSettingsPreferenceController) {
                ((FaceSettingsPreferenceController) next).setUserId(this.mUserId);
            } else if (next instanceof FaceSettingsEnrollButtonPreferenceController) {
                ((FaceSettingsEnrollButtonPreferenceController) next).setUserId(this.mUserId);
            }
        }
        this.mRemoveController.setUserId(this.mUserId);
        FaceSettingsRedoPreferenceController faceSettingsRedoPreferenceController = this.mRedoController;
        if (faceSettingsRedoPreferenceController != null) {
            faceSettingsRedoPreferenceController.setUserId(this.mUserId);
        }
        if (this.mUserManager.isManagedProfile(this.mUserId)) {
            removePreference("security_settings_face_keyguard");
            removePreference(this.mLockscreenController.getPreferenceKey());
        }
        if (bundle != null) {
            this.mToken = bundle.getByteArray("hw_auth_token");
        }
        if (FaceUtils.isMotoFaceUnlock()) {
            MotoFaceLicensesMenuController.init(this);
            removePreference("security_settings_face_unlock_category");
            Settings.Secure.putIntForUser(getContentResolver(), "face_unlock_keyguard_enabled", 1, this.mUserId);
        }
    }

    public void onResume() {
        super.onResume();
        byte[] bArr = this.mToken;
        if (bArr != null || this.mConfirmingPassword) {
            this.mAttentionController.setToken(bArr);
            this.mEnrollController.setToken(this.mToken);
            this.mRedoController.setToken(this.mToken);
        } else {
            boolean show = new ChooseLockSettingsHelper.Builder(getActivity(), this).setRequestCode(4).setTitle(getString(C1992R$string.security_settings_face_preference_title)).setRequestGatekeeperPasswordHandle(true).setUserId(this.mUserId).setForegroundOnly(true).setReturnCredentials(true).show();
            this.mConfirmingPassword = true;
            if (!show) {
                Log.e("FaceSettings", "Password not set");
                finish();
            }
        }
        boolean hasEnrolledTemplates = this.mFaceManager.hasEnrolledTemplates(this.mUserId);
        this.mEnrollButton.setVisible(!hasEnrolledTemplates);
        this.mRemoveButton.setVisible(hasEnrolledTemplates);
        if (!this.mFaceFeatureProvider.isAttentionSupported(getContext())) {
            removePreference(FaceSettingsAttentionPreferenceController.KEY);
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (this.mToken == null && !BiometricUtils.containsGatekeeperPasswordHandle(intent)) {
            Log.e("FaceSettings", "No credential");
            finish();
        }
        if (i == 4) {
            if (i2 == 1 || i2 == -1) {
                this.mFaceManager.generateChallenge(this.mUserId, new FaceSettings$$ExternalSyntheticLambda0(this, intent));
            }
        } else if (i == 5 && i2 == 3) {
            setResult(i2, intent);
            finish();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onActivityResult$2(Intent intent, int i, int i2, long j) {
        this.mToken = BiometricUtils.requestGatekeeperHat(getPrefContext(), intent, this.mUserId, j);
        this.mSensorId = i;
        this.mChallenge = j;
        BiometricUtils.removeGatekeeperPasswordHandle(getPrefContext(), intent);
        this.mAttentionController.setToken(this.mToken);
        this.mEnrollController.setToken(this.mToken);
        this.mConfirmingPassword = false;
    }

    public void onStop() {
        super.onStop();
        if (!this.mEnrollController.isClicked() && !getActivity().isChangingConfigurations() && !this.mConfirmingPassword) {
            if (this.mToken != null) {
                this.mFaceManager.revokeChallenge(this.mSensorId, this.mUserId, this.mChallenge);
                this.mToken = null;
            }
            finish();
        }
    }

    public int getHelpResource() {
        return C1992R$string.help_url_face;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        if (!isFaceHardwareDetected(context)) {
            return null;
        }
        List<AbstractPreferenceController> buildPreferenceControllers = buildPreferenceControllers(context, getSettingsLifecycle());
        this.mControllers = buildPreferenceControllers;
        for (AbstractPreferenceController next : buildPreferenceControllers) {
            if (next instanceof FaceSettingsAttentionPreferenceController) {
                this.mAttentionController = (FaceSettingsAttentionPreferenceController) next;
            } else if (next instanceof FaceSettingsRemoveButtonPreferenceController) {
                FaceSettingsRemoveButtonPreferenceController faceSettingsRemoveButtonPreferenceController = (FaceSettingsRemoveButtonPreferenceController) next;
                this.mRemoveController = faceSettingsRemoveButtonPreferenceController;
                faceSettingsRemoveButtonPreferenceController.setListener(this.mRemovalListener);
                this.mRemoveController.setActivity((SettingsActivity) getActivity());
            } else if (next instanceof FaceSettingsEnrollButtonPreferenceController) {
                FaceSettingsEnrollButtonPreferenceController faceSettingsEnrollButtonPreferenceController = (FaceSettingsEnrollButtonPreferenceController) next;
                this.mEnrollController = faceSettingsEnrollButtonPreferenceController;
                faceSettingsEnrollButtonPreferenceController.setListener(this.mEnrollListener);
                this.mEnrollController.setActivity((SettingsActivity) getActivity());
            } else if (next instanceof FaceSettingsRedoPreferenceController) {
                FaceSettingsRedoPreferenceController faceSettingsRedoPreferenceController = (FaceSettingsRedoPreferenceController) next;
                this.mRedoController = faceSettingsRedoPreferenceController;
                faceSettingsRedoPreferenceController.setActivity((SettingsActivity) getActivity());
            }
        }
        return this.mControllers;
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new FaceSettingsKeyguardPreferenceController(context));
        arrayList.add(new FaceSettingsAppPreferenceController(context));
        arrayList.add(new FaceSettingsAttentionPreferenceController(context));
        arrayList.add(new FaceSettingsRemoveButtonPreferenceController(context));
        arrayList.add(new FaceSettingsConfirmPreferenceController(context));
        arrayList.add(new FaceSettingsLTVPreferenceController(context));
        arrayList.add(new FaceSettingsEnrollButtonPreferenceController(context));
        arrayList.add(new FaceSettingsRedoPreferenceController(context));
        return arrayList;
    }
}

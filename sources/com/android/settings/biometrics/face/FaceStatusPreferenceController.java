package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.face.FaceManager;
import android.os.UserHandle;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C1992R$string;
import com.android.settings.Settings;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricStatusPreferenceController;
import com.android.settings.biometrics.ParentalControlsUtils;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreference;
import com.motorola.settings.biometrics.face.FaceUtils;
import com.motorola.settings.utils.PrivacySpaceUtils;

public class FaceStatusPreferenceController extends BiometricStatusPreferenceController implements LifecycleObserver {
    public static final String KEY_FACE_SETTINGS = "face_settings";
    protected final FaceManager mFaceManager;
    @VisibleForTesting
    RestrictedPreference mPreference;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public FaceStatusPreferenceController(Context context) {
        this(context, KEY_FACE_SETTINGS, (Lifecycle) null);
    }

    public FaceStatusPreferenceController(Context context, String str) {
        this(context, str, (Lifecycle) null);
    }

    public FaceStatusPreferenceController(Context context, Lifecycle lifecycle) {
        this(context, KEY_FACE_SETTINGS, lifecycle);
    }

    public FaceStatusPreferenceController(Context context, String str, Lifecycle lifecycle) {
        super(context, str);
        this.mFaceManager = Utils.getFaceManagerOrNull(context);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        updateStateInternal();
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedPreference) preferenceScreen.findPreference(this.mPreferenceKey);
    }

    /* access modifiers changed from: protected */
    public boolean isDeviceSupported() {
        return !Utils.isMultipleBiometricsSupported(this.mContext) && Utils.hasFaceHardware(this.mContext);
    }

    /* access modifiers changed from: protected */
    public boolean isUserSupported() {
        return !PrivacySpaceUtils.isPrivacySpaceUser(this.mContext, UserHandle.myUserId());
    }

    /* access modifiers changed from: protected */
    public boolean hasEnrolledBiometrics() {
        return this.mFaceManager.hasEnrolledTemplates(getUserId());
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        updateStateInternal();
    }

    private void updateStateInternal() {
        updateStateInternal(ParentalControlsUtils.parentConsentRequired(this.mContext, 8));
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateStateInternal(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        if (this.mPreference == null) {
            return;
        }
        if (FaceUtils.isMotoFaceUnlock()) {
            this.mPreference.setEnabled(!FaceUtils.isFaceDisabledByAdmin(this.mContext));
        } else {
            this.mPreference.setDisabledByAdmin(enforcedAdmin);
        }
    }

    /* access modifiers changed from: protected */
    public String getSummaryTextEnrolled() {
        if (!FaceUtils.isMotoFaceUnlock() || !FaceUtils.isFaceDisabledByAdmin(this.mContext)) {
            return this.mContext.getResources().getString(C1992R$string.security_settings_face_preference_summary);
        }
        return this.mContext.getResources().getString(C1992R$string.disabled_by_administrator_summary);
    }

    /* access modifiers changed from: protected */
    public String getSummaryTextNoneEnrolled() {
        if (!FaceUtils.isMotoFaceUnlock() || !FaceUtils.isFaceDisabledByAdmin(this.mContext)) {
            return this.mContext.getResources().getString(C1992R$string.security_settings_face_preference_summary_none);
        }
        return this.mContext.getResources().getString(C1992R$string.disabled_by_administrator_summary);
    }

    /* access modifiers changed from: protected */
    public String getSettingsClassName() {
        return Settings.FaceSettingsActivity.class.getName();
    }

    /* access modifiers changed from: protected */
    public String getEnrollClassName() {
        return FaceEnrollIntroduction.class.getName();
    }
}

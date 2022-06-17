package com.android.settings.biometrics.fingerprint;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.fingerprint.FingerprintManager;
import android.os.UserHandle;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C1990R$plurals;
import com.android.settings.C1992R$string;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricStatusPreferenceController;
import com.android.settings.biometrics.ParentalControlsUtils;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreference;
import com.motorola.settings.utils.PrivacySpaceUtils;

public class FingerprintStatusPreferenceController extends BiometricStatusPreferenceController implements LifecycleObserver {
    private static final String KEY_FINGERPRINT_SETTINGS = "fingerprint_settings";
    protected final FingerprintManager mFingerprintManager;
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

    public FingerprintStatusPreferenceController(Context context) {
        this(context, KEY_FINGERPRINT_SETTINGS);
    }

    public FingerprintStatusPreferenceController(Context context, String str) {
        this(context, str, (Lifecycle) null);
    }

    public FingerprintStatusPreferenceController(Context context, Lifecycle lifecycle) {
        this(context, KEY_FINGERPRINT_SETTINGS, lifecycle);
    }

    public FingerprintStatusPreferenceController(Context context, String str, Lifecycle lifecycle) {
        super(context, str);
        this.mFingerprintManager = Utils.getFingerprintManagerOrNull(context);
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
        return !Utils.isMultipleBiometricsSupported(this.mContext) && Utils.hasFingerprintHardware(this.mContext);
    }

    /* access modifiers changed from: protected */
    public boolean isUserSupported() {
        return !PrivacySpaceUtils.isPrivacySpaceUser(this.mContext, UserHandle.myUserId());
    }

    /* access modifiers changed from: protected */
    public boolean hasEnrolledBiometrics() {
        return this.mFingerprintManager.hasEnrolledFingerprints(getUserId());
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        updateStateInternal();
    }

    private void updateStateInternal() {
        updateStateInternal(ParentalControlsUtils.parentConsentRequired(this.mContext, 2));
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateStateInternal(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        RestrictedPreference restrictedPreference = this.mPreference;
        if (restrictedPreference != null) {
            restrictedPreference.setDisabledByAdmin(enforcedAdmin);
        }
    }

    /* access modifiers changed from: protected */
    public String getSummaryTextEnrolled() {
        int size = this.mFingerprintManager.getEnrolledFingerprints(getUserId()).size();
        return this.mContext.getResources().getQuantityString(C1990R$plurals.security_settings_fingerprint_preference_summary, size, new Object[]{Integer.valueOf(size)});
    }

    /* access modifiers changed from: protected */
    public String getSummaryTextNoneEnrolled() {
        return this.mContext.getString(C1992R$string.security_settings_fingerprint_preference_summary_none);
    }

    /* access modifiers changed from: protected */
    public String getSettingsClassName() {
        return FingerprintSettings.class.getName();
    }

    /* access modifiers changed from: protected */
    public String getEnrollClassName() {
        return FingerprintEnrollIntroduction.class.getName();
    }
}

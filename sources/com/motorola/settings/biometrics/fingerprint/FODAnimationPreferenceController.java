package com.motorola.settings.biometrics.fingerprint;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class FODAnimationPreferenceController extends BasePreferenceController {
    private static final int[] ANIMATION_NAMES = {C1992R$string.fingerprint_fod_animation_style_1, C1992R$string.fingerprint_fod_animation_style_2, C1992R$string.fingerprint_fod_animation_style_3};
    public static final String KEY = "key_fingerprint_styles";

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

    public FODAnimationPreferenceController(Context context) {
        super(context, KEY);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        if (findPreference != null) {
            findPreference.setTitle(C1992R$string.fingerprint_fod_animation_settings);
            findPreference.setIntent(new Intent(this.mContext, FODAnimationSettings.class));
        }
    }

    public int getAvailabilityStatus() {
        return FingerprintUtils.isMotoFODEnabled(this.mContext) ? 0 : 3;
    }

    public CharSequence getSummary() {
        int fingerprintAnimationStyle = FingerprintUtils.getFingerprintAnimationStyle(this.mContext);
        int[] iArr = ANIMATION_NAMES;
        if (fingerprintAnimationStyle < iArr.length) {
            return this.mContext.getResources().getString(iArr[fingerprintAnimationStyle]);
        }
        return null;
    }
}

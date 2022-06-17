package com.motorola.settings.biometrics.fingerprint;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.SwitchPreference;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class FingerprintTouchToUnlockSwitchPreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    public static final String KEY = "key_touch_to_unlock";

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

    public FingerprintTouchToUnlockSwitchPreferenceController(Context context) {
        super(context, KEY);
    }

    public void displayPreference(PreferenceGroup preferenceGroup) {
        SwitchPreference switchPreference = new SwitchPreference(preferenceGroup.getContext());
        switchPreference.setKey(KEY);
        switchPreference.setTitle(C1992R$string.fingerprint_touch_to_unlock_preference_title);
        switchPreference.setSummary(C1992R$string.fingerprint_touch_to_unlock_preference_summary);
        switchPreference.setChecked(FingerprintUtils.getTouchToUnlockState(preferenceGroup.getContext()));
        preferenceGroup.addPreference(switchPreference);
        switchPreference.setOnPreferenceChangeListener(this);
    }

    public int getAvailabilityStatus() {
        return FingerprintUtils.isSideFingerPrint(this.mContext) ? 0 : 3;
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        ((SwitchPreference) preference).setChecked(booleanValue);
        FingerprintUtils.setTouchToUnlockState(this.mContext, booleanValue);
        return true;
    }
}

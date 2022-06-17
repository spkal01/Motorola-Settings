package com.motorola.settings.network.emara;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.network.MotoRoamingControllerUtils;
import com.motorola.settings.network.MotoTelephonyBasePreferenceController;
import com.motorola.settings.network.MotoTelephonyFeature;

public class NationalDataRoamingController extends MotoTelephonyBasePreferenceController {
    private static final String TAG = "NationalDataRoamingController";
    private Preference mNationalDataRoaming;
    private Bundle mPrefValues;

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

    public NationalDataRoamingController(Context context, String str) {
        super(context, str);
    }

    public void init(int i) {
        this.mPrefValues = MotoRoamingControllerUtils.getNationalDataRoamingPreference(this.mContext, i);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mNationalDataRoaming = preferenceScreen.findPreference(getPreferenceKey());
    }

    public void updateState(Preference preference) {
        this.mPrefValues = MotoRoamingControllerUtils.getNationalDataRoamingPreference(this.mContext, this.mSubId);
        updateNationalDataRoamingRoaming();
    }

    public int getAvailabilityStatus(int i) {
        Bundle bundle = this.mPrefValues;
        boolean z = false;
        if (bundle != null) {
            z = bundle.getBoolean("is_visible", false);
        }
        return (!MotoTelephonyFeature.isFtr4705Enabled(this.mContext, i) || !z) ? 2 : 1;
    }

    private void updateNationalDataRoamingRoaming() {
        Bundle bundle;
        Preference preference = this.mNationalDataRoaming;
        if (preference != null && (bundle = this.mPrefValues) != null) {
            preference.setTitle(bundle.getCharSequence("title"));
            this.mNationalDataRoaming.setSummary(this.mPrefValues.getCharSequence("summary"));
            this.mNationalDataRoaming.setIntent((Intent) this.mPrefValues.getParcelable("intent"));
        }
    }
}

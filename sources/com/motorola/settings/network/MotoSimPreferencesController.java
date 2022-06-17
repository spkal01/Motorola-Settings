package com.motorola.settings.network;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import com.android.settings.network.telephony.TelephonyBasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class MotoSimPreferencesController extends TelephonyBasePreferenceController {
    private static final String ACTION_SIM_SETTINGS = "com.motorola.msimsettings.SIM_SETTINGS";
    private Fragment mFragment;

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

    public MotoSimPreferencesController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus(int i) {
        return MotoTelephonyFeature.isFtr6379Enabled(this.mContext, i) ? 0 : 2;
    }

    public void init(Fragment fragment) {
        this.mFragment = fragment;
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }
        this.mFragment.startActivityForResult(new Intent(ACTION_SIM_SETTINGS), 0);
        return true;
    }
}

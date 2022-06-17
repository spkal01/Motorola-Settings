package com.motorola.settings.network.vzw;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.network.ExtendedVideoCallingPreferenceController;
import com.motorola.settings.network.MotoMnsLog;
import com.motorola.settings.network.MotoTelephonyFeature;

public class VzwVideoCallingPreferenceController extends ExtendedVideoCallingPreferenceController {
    private static final String TAG = "VzwVideoCallingPref";

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

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public VzwVideoCallingPreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus(int i) {
        if (MotoTelephonyFeature.isVzwVtSupported(this.mContext, i)) {
            return 3;
        }
        return super.getAvailabilityStatus(i);
    }

    public void updateState(Preference preference) {
        if (MotoTelephonyFeature.isVzwVtSupported(this.mContext, this.mSubId)) {
            MotoMnsLog.logd(TAG, "Ignore updateState for Vzw...");
        } else {
            super.updateState(preference);
        }
    }
}

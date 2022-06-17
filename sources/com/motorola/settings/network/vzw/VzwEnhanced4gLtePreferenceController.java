package com.motorola.settings.network.vzw;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.network.ExtendedEnhanced4gLtePreferenceController;
import com.motorola.settings.network.MotoTelephonyFeature;

public class VzwEnhanced4gLtePreferenceController extends ExtendedEnhanced4gLtePreferenceController {
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

    public VzwEnhanced4gLtePreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus(int i) {
        if (MotoTelephonyFeature.isVzwVtSupported(this.mContext, i) || MotoTelephonyFeature.isCdmaLessDevice(this.mContext, i) || MotoTelephonyFeature.advancedCallSettings(this.mContext, i)) {
            return 3;
        }
        return super.getAvailabilityStatus(i);
    }
}

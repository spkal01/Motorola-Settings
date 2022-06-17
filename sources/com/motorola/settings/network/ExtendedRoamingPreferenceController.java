package com.motorola.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.network.telephony.RoamingPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class ExtendedRoamingPreferenceController extends RoamingPreferenceController {
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

    public ExtendedRoamingPreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus(int i) {
        if (MotoTelephonyFeature.isFtr5717Enabled(this.mContext, i)) {
            return 2;
        }
        return super.getAvailabilityStatus(i);
    }
}

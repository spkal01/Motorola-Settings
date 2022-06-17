package com.motorola.settings.network.att;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.network.telephony.Enable2gPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.network.MotoTelephonyFeature;

public class Att2GEnablePreferenceController extends Enable2gPreferenceController {
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

    public Att2GEnablePreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus(int i) {
        if (MotoTelephonyFeature.isFtr5083Enabled(this.mContext, i)) {
            return 3;
        }
        return super.getAvailabilityStatus(i);
    }
}

package com.motorola.settings.network.att;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.motorola.settings.network.ExtendedAutoSelectPreferenceController;
import com.motorola.settings.network.MotoTelephonyFeature;

public class AttAutoSelectPreferenceController extends ExtendedAutoSelectPreferenceController {
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

    public AttAutoSelectPreferenceController(Context context, String str) {
        super(context, str);
    }

    public AttAutoSelectPreferenceController init(Lifecycle lifecycle, int i) {
        return (AttAutoSelectPreferenceController) super.init(lifecycle, i);
    }

    public int getAvailabilityStatus(int i) {
        if (MotoTelephonyFeature.needShowOperatorSelectionforPtn(this.mContext, i)) {
            return 0;
        }
        return super.getAvailabilityStatus(i);
    }
}

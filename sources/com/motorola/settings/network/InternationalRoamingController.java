package com.motorola.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.slices.SliceBackgroundWorker;

public class InternationalRoamingController extends MotoTelephonyTogglePreferenceController {
    private static final String TAG = "InternationalRoamingController";

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

    public InternationalRoamingController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus(int i) {
        return MotoTelephonyFeature.isFtr5717Enabled(this.mContext, i) ? 0 : 2;
    }

    public boolean setChecked(boolean z) {
        MotoRoamingControllerUtils.setDataRoamingEnabled(this.mContext, this.mSubId, false, z);
        return true;
    }

    public boolean isChecked() {
        return MotoRoamingControllerUtils.getDataRoamingEnabled(this.mContext, this.mSubId, false);
    }
}

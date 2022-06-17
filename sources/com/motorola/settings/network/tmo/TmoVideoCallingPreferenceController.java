package com.motorola.settings.network.tmo;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.network.ExtendedVideoCallingPreferenceController;
import com.motorola.settings.network.MotoTelephonyFeature;

public class TmoVideoCallingPreferenceController extends ExtendedVideoCallingPreferenceController {
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

    public TmoVideoCallingPreferenceController(Context context, String str) {
        super(context, str);
    }

    public boolean isVideoCallEnabled(int i) {
        if (MotoTelephonyFeature.isTmoVtSupported(this.mContext, i)) {
            return false;
        }
        return super.isVideoCallEnabled(i);
    }
}

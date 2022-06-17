package com.motorola.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.network.telephony.VideoCallingPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class ExtendedVideoCallingPreferenceController extends VideoCallingPreferenceController {
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

    public ExtendedVideoCallingPreferenceController(Context context, String str) {
        super(context, str);
    }
}

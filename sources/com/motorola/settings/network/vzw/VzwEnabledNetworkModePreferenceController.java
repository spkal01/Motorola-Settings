package com.motorola.settings.network.vzw;

import android.content.Context;
import android.content.IntentFilter;
import android.os.PersistableBundle;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.network.ExtendedEnabledNetworkModePreferenceController;
import com.motorola.settings.network.MotoTelephonyFeature;

public class VzwEnabledNetworkModePreferenceController extends ExtendedEnabledNetworkModePreferenceController {
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

    public VzwEnabledNetworkModePreferenceController(Context context, String str) {
        super(context, str);
    }

    /* access modifiers changed from: protected */
    public boolean isWorldPhone(PersistableBundle persistableBundle) {
        return super.isWorldPhone(persistableBundle) || MotoTelephonyFeature.isVzwWorldPhoneEnabled(persistableBundle);
    }
}

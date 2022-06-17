package com.motorola.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.android.provider.MotorolaSettings;

public class EonsController extends MotoTelephonyTogglePreferenceController {
    private static final String TAG = "EonsController";

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

    public EonsController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus(int i) {
        return MotoTelephonyFeature.isFtr4081Enabled(this.mContext, i) ? 0 : 2;
    }

    public boolean setChecked(boolean z) {
        MotorolaSettings.Global.putInt(this.mContext.getContentResolver(), "display_network_name", z ? 1 : 0);
        return true;
    }

    public boolean isChecked() {
        return MotorolaSettings.Global.getInt(this.mContext.getContentResolver(), "display_network_name", 0) == 1;
    }
}

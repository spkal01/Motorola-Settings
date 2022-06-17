package com.motorola.settings.display.refreshrate;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class DisplayRefreshRatePreferenceController extends BasePreferenceController {
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

    public DisplayRefreshRatePreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus() {
        return DisplayRefreshRateUtil.isDisplayRefreshRateAvailable(this.mContext) ? 1 : 3;
    }

    public CharSequence getSummary() {
        return getSummaryText();
    }

    private CharSequence getSummaryText() {
        return DisplayRefreshRateUtil.getCurrentRefreshRateTitle(this.mContext);
    }
}

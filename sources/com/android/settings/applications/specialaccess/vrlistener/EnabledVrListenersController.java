package com.android.settings.applications.specialaccess.vrlistener;

import android.app.ActivityManager;
import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.C1980R$bool;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class EnabledVrListenersController extends BasePreferenceController {
    private final ActivityManager mActivityManager = ((ActivityManager) this.mContext.getSystemService(ActivityManager.class));

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

    public EnabledVrListenersController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus() {
        return (!this.mContext.getResources().getBoolean(C1980R$bool.config_show_enabled_vr_listeners) || this.mActivityManager.isLowRamDevice()) ? 3 : 0;
    }
}

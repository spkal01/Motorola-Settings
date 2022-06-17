package com.android.settings.applications.managedomainurls;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Settings;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class WebActionCategoryController extends BasePreferenceController {
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

    public WebActionCategoryController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus() {
        if (!Build.IS_PRC_PRODUCT && !isDisableWebActions(this.mContext)) {
            return 0;
        }
        return 3;
    }

    public static boolean isDisableWebActions(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "enable_ephemeral_feature", 1) == 0;
    }
}

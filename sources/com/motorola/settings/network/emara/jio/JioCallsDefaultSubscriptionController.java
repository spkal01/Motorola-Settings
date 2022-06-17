package com.motorola.settings.network.emara.jio;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.network.telephony.CallsDefaultSubscriptionController;
import com.android.settings.slices.SliceBackgroundWorker;

public class JioCallsDefaultSubscriptionController extends CallsDefaultSubscriptionController {
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

    public JioCallsDefaultSubscriptionController(Context context, String str) {
        super(context, str);
    }

    /* access modifiers changed from: protected */
    public void updateEntries() {
        super.updateEntries();
        if (JioFeatureLockUtils.isSubsidyLocked(this.mContext)) {
            this.mPreference.setEnabled(false);
        }
    }
}

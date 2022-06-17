package com.android.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class OneHandedEnablePreferenceController extends BasePreferenceController {
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

    public OneHandedEnablePreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus() {
        return OneHandedSettingsUtils.isSupportOneHandedMode() ? 0 : 3;
    }

    public CharSequence getSummary() {
        Context context = this.mContext;
        return context.getText(OneHandedSettingsUtils.isOneHandedModeEnabled(context) ? C1992R$string.gesture_setting_on : C1992R$string.gesture_setting_off);
    }
}

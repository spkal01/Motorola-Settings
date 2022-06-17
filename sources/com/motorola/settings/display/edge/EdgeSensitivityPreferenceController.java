package com.motorola.settings.display.edge;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import com.android.settings.C1978R$array;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class EdgeSensitivityPreferenceController extends BasePreferenceController {
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

    public EdgeSensitivityPreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus() {
        return EdgeUtils.isEdgeSensitivityAvailable(this.mContext) ? 1 : 3;
    }

    public CharSequence getSummary() {
        int displayGripSuppressionDistance = EdgeUtils.getDisplayGripSuppressionDistance(this.mContext);
        Resources resources = this.mContext.getResources();
        int[] sensitivityScales = EdgeUtils.getSensitivityScales(this.mContext);
        int i = 0;
        for (int i2 = 0; i2 < sensitivityScales.length; i2++) {
            if (sensitivityScales[i2] == displayGripSuppressionDistance) {
                i = i2;
            }
        }
        return resources.getStringArray(C1978R$array.config_edgeSensitivityScaleDisplayTexts)[i];
    }
}

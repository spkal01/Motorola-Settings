package com.motorola.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.internal.enterprise.MotoDevicePolicyManager;

public class ThinkShieldMobileVersionPreferenceController extends BasePreferenceController {
    private final MotoDevicePolicyManager mMotoDpms = ((MotoDevicePolicyManager) this.mContext.getSystemService("mot_device_policy"));

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

    public ThinkShieldMobileVersionPreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus() {
        return this.mMotoDpms != null ? 1 : 3;
    }

    public CharSequence getSummary() {
        MotoDevicePolicyManager motoDevicePolicyManager = this.mMotoDpms;
        return motoDevicePolicyManager != null ? motoDevicePolicyManager.getThinkshieldMobileVersion() : "00.00.000";
    }
}

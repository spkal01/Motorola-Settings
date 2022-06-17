package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.slices.SliceBackgroundWorker;

public class NotificationVibrationIntensityPreferenceController extends VibrationIntensityPreferenceController {
    static final String PREF_KEY = "notification_vibration_preference_screen";

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public int getAvailabilityStatus() {
        return 0;
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

    public NotificationVibrationIntensityPreferenceController(Context context) {
        super(context, PREF_KEY, "notification_vibration_intensity", "");
    }

    /* access modifiers changed from: protected */
    public int getDefaultIntensity() {
        return this.mVibrator.getDefaultNotificationVibrationIntensity();
    }
}

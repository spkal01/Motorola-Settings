package com.motorola.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.text.TextUtils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class MotoBuildRevisionPreferenceController extends BasePreferenceController {
    private static final boolean BUILD_IS_SIGNED = SystemProperties.get("ro.build.tags", "").contains("release-keys");
    static final String BUILD_REVISION = SystemProperties.get("ro.build.version.revision", "");

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

    public MotoBuildRevisionPreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus() {
        if (!BUILD_IS_SIGNED) {
            String str = BUILD_REVISION;
            return (TextUtils.isEmpty(str) || str.equals("0")) ? 3 : 1;
        }
    }

    public CharSequence getSummary() {
        return BUILD_REVISION;
    }
}

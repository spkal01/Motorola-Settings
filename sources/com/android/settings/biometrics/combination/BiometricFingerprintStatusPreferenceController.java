package com.android.settings.biometrics.combination;

import android.content.Context;
import android.content.IntentFilter;
import androidx.lifecycle.Lifecycle;
import com.android.settings.Utils;
import com.android.settings.biometrics.fingerprint.FingerprintStatusPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class BiometricFingerprintStatusPreferenceController extends FingerprintStatusPreferenceController {
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

    public BiometricFingerprintStatusPreferenceController(Context context, String str) {
        super(context, str);
    }

    public BiometricFingerprintStatusPreferenceController(Context context, String str, Lifecycle lifecycle) {
        super(context, str, lifecycle);
    }

    /* access modifiers changed from: protected */
    public boolean isDeviceSupported() {
        return Utils.isMultipleBiometricsSupported(this.mContext) && Utils.hasFingerprintHardware(this.mContext);
    }
}

package com.android.settings.biometrics.combination;

import android.content.Context;
import android.content.IntentFilter;
import androidx.lifecycle.Lifecycle;
import com.android.settings.slices.SliceBackgroundWorker;

public class BiometricFingerprintProfileStatusPreferenceController extends BiometricFingerprintStatusPreferenceController {
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

    public BiometricFingerprintProfileStatusPreferenceController(Context context, String str) {
        super(context, str);
    }

    public BiometricFingerprintProfileStatusPreferenceController(Context context, String str, Lifecycle lifecycle) {
        super(context, str, lifecycle);
    }

    /* access modifiers changed from: protected */
    public boolean isUserSupported() {
        int i = this.mProfileChallengeUserId;
        return i != -10000 && this.mLockPatternUtils.isSeparateProfileChallengeAllowed(i);
    }

    /* access modifiers changed from: protected */
    public int getUserId() {
        return this.mProfileChallengeUserId;
    }
}

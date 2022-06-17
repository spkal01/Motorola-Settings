package com.motorola.settings.deviceinfo.hardwareinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;
import androidx.preference.Preference;
import com.android.internal.util.MemInfoReader;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.utils.ThreadUtils;

public class HardwareRamPreferenceController extends BasePreferenceController {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String TAG = "HardwareRamInfo";

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

    public CharSequence getSummary() {
        return " ";
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

    public HardwareRamPreferenceController(Context context, String str) {
        super(context, str);
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        ThreadUtils.postOnBackgroundThread((Runnable) new HardwareRamPreferenceController$$ExternalSyntheticLambda1(this, preference));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$1(Preference preference) {
        MemInfoReader memInfoReader = new MemInfoReader();
        memInfoReader.readMemInfo();
        ThreadUtils.postOnMainThread(new HardwareRamPreferenceController$$ExternalSyntheticLambda0(this, getCorrectedRamSize(memInfoReader.getTotalSize()), preference));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$0(long j, Preference preference) {
        if (j != 0) {
            preference.setSummary((CharSequence) Formatter.formatFileSize(this.mContext, j));
            Log.d(TAG, "ramInfo  = " + j);
        }
    }

    private static long getSIEquivalentCorrectedSize(long j) {
        int i = 0;
        while (j >= 1024) {
            j /= 1024;
            i++;
        }
        while (i > 0) {
            j *= 1000;
            i--;
        }
        return j;
    }

    private long getCorrectedRamSize(long j) {
        int i = 0;
        long[] jArr = {268435456, 536870912, 1073741824, 1610612736, 2147483648L, 3221225472L, 4294967296L, 6442450944L, 8589934592L, 12884901888L, 17179869184L, 25769803776L, 34359738368L};
        if (DEBUG) {
            Log.d(TAG, "getCorrectedRamSize size:" + j);
        }
        if (j <= 0) {
            return 0;
        }
        while (i < 13 && j > jArr[i]) {
            i++;
        }
        if (i == 13) {
            i--;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            return getSIEquivalentCorrectedSize(jArr[i]);
        }
        return jArr[i];
    }
}

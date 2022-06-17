package com.motorola.settings.deviceinfo.hardwareinfo;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.IntentFilter;
import android.os.storage.StorageManager;
import android.text.format.Formatter;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.utils.ThreadUtils;
import java.io.IOException;

public class HardwareRomPreferenceController extends BasePreferenceController {
    private static final String TAG = "HardwareRomInfo";

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

    public HardwareRomPreferenceController(Context context, String str) {
        super(context, str);
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        ThreadUtils.postOnBackgroundThread((Runnable) new HardwareRomPreferenceController$$ExternalSyntheticLambda0(this, (StorageStatsManager) this.mContext.getSystemService(StorageStatsManager.class), preference));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$1(StorageStatsManager storageStatsManager, Preference preference) {
        try {
            ThreadUtils.postOnMainThread(new HardwareRomPreferenceController$$ExternalSyntheticLambda1(this, preference, storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT)));
        } catch (IOException e) {
            Log.w(TAG, e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$0(Preference preference, long j) {
        preference.setSummary((CharSequence) Formatter.formatFileSize(this.mContext, j));
    }
}

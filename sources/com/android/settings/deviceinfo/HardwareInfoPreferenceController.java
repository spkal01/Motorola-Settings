package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1980R$bool;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.deviceinfo.DeviceUtils;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class HardwareInfoPreferenceController extends BasePreferenceController {
    private static final String TAG = "DeviceModelPrefCtrl";

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

    public HardwareInfoPreferenceController(Context context, String str) {
        super(context, str);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
    }

    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(C1980R$bool.config_show_device_model) ? 0 : 3;
    }

    public CharSequence getSummary() {
        return DeviceUtils.getDeviceModelSummary();
    }

    public static String getDeviceModel() {
        String deviceDisplayModel = DeviceUtils.getDeviceDisplayModel();
        FutureTask futureTask = new FutureTask(HardwareInfoPreferenceController$$ExternalSyntheticLambda0.INSTANCE);
        futureTask.run();
        try {
            return deviceDisplayModel + ((String) futureTask.get());
        } catch (ExecutionException unused) {
            Log.e(TAG, "Execution error, so we only show model name");
            return deviceDisplayModel;
        } catch (InterruptedException unused2) {
            Log.e(TAG, "Interruption error, so we only show model name");
            return deviceDisplayModel;
        }
    }
}

package com.motorola.settings.deviceinfo.hardwareinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.HashMap;

public class HardwareDisplayPreferenceController extends BasePreferenceController {
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

    public HardwareDisplayPreferenceController(Context context, String str) {
        super(context, str);
    }

    public CharSequence getSummary() {
        WindowManager windowManager = (WindowManager) this.mContext.getSystemService("window");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        }
        int i = displayMetrics.widthPixels;
        int i2 = displayMetrics.heightPixels;
        String string = this.mContext.getResources().getString(C1992R$string.hardware_display_x, new Object[]{Integer.valueOf(i2), Integer.valueOf(i)});
        String displayType = getDisplayType(i, i2);
        if (displayType.isEmpty()) {
            return string;
        }
        return string + " " + displayType;
    }

    private String getDisplayType(int i, int i2) {
        HashMap hashMap = new HashMap();
        hashMap.put(16120, "QQVGA");
        hashMap.put(24160, "HQVGA");
        hashMap.put(32240, "QVGA");
        hashMap.put(38640, "WQVGA");
        hashMap.put(40240, "WQVGA");
        hashMap.put(43440, "WQVGA");
        hashMap.put(48320, "HVGA");
        hashMap.put(64360, "nHD");
        hashMap.put(64480, "VGA");
        hashMap.put(80480, "WVGA");
        hashMap.put(85880, "FWVGA");
        hashMap.put(96540, "qHD");
        hashMap.put(102976, "WSVGA");
        hashMap.put(80600, "SVGA");
        hashMap.put(103000, "WSVGA");
        hashMap.put(96640, "DVGA");
        hashMap.put(128720, "HD");
        int min = Math.min(i, i2) + (Math.max(i, i2) * 100);
        return hashMap.containsKey(Integer.valueOf(min)) ? (String) hashMap.get(Integer.valueOf(min)) : "";
    }
}

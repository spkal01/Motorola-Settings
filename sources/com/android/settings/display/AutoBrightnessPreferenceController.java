package com.android.settings.display;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import com.android.settings.C1980R$bool;
import com.android.settings.C1992R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class AutoBrightnessPreferenceController extends TogglePreferenceController {
    private final int DEFAULT_VALUE = 0;
    private final String SYSTEM_KEY = "screen_brightness_mode";

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

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AutoBrightnessPreferenceController(Context context, String str) {
        super(context, str);
    }

    public boolean isChecked() {
        return Settings.System.getInt(this.mContext.getContentResolver(), "screen_brightness_mode", 0) != 0;
    }

    public boolean setChecked(boolean z) {
        Settings.System.putInt(this.mContext.getContentResolver(), "screen_brightness_mode", z ? 1 : 0);
        return true;
    }

    public int getAvailabilityStatus() {
        return (this.mContext.getResources().getBoolean(C1980R$bool.config_adaptive_brightness_customizable) || !this.mContext.getResources().getBoolean(17891387)) ? 3 : 1;
    }

    public CharSequence getSummary() {
        int i;
        Context context = this.mContext;
        if (isChecked()) {
            i = C1992R$string.auto_brightness_summary_on;
        } else {
            i = C1992R$string.auto_brightness_summary_off;
        }
        return context.getText(i);
    }
}

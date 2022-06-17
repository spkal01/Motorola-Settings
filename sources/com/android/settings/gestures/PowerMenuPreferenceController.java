package com.android.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class PowerMenuPreferenceController extends BasePreferenceController {
    @VisibleForTesting
    static final int LONG_PRESS_POWER_ASSISTANT_VALUE = 5;
    @VisibleForTesting
    static final int LONG_PRESS_POWER_GLOBAL_ACTIONS = 1;
    private static final String POWER_BUTTON_LONG_PRESS_SETTING = "power_button_long_press";

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

    public PowerMenuPreferenceController(Context context, String str) {
        super(context, str);
    }

    public CharSequence getSummary() {
        int powerButtonLongPressValue = getPowerButtonLongPressValue(this.mContext);
        if (powerButtonLongPressValue == 5) {
            return this.mContext.getText(C1992R$string.power_menu_summary_long_press_for_assist_enabled);
        }
        if (powerButtonLongPressValue == 1) {
            return this.mContext.getText(C1992R$string.f62x331c8baf);
        }
        return this.mContext.getText(C1992R$string.power_menu_summary_long_press_for_assist_disabled_no_action);
    }

    public int getAvailabilityStatus() {
        return isAssistInvocationAvailable() ? 0 : 3;
    }

    private boolean isAssistInvocationAvailable() {
        return this.mContext.getResources().getBoolean(17891646);
    }

    private static int getPowerButtonLongPressValue(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), POWER_BUTTON_LONG_PRESS_SETTING, context.getResources().getInteger(17694859));
    }
}

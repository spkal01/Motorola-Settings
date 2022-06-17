package com.motorola.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.C1992R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.utils.TriplePowerPressEmergencyCallUtil;

public class TripleTapPowerEmergencyCallPrefController extends TogglePreferenceController {
    public static final String PREF_KEY = "gesture_triple_press_power_key_emergency_call";

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

    public TripleTapPowerEmergencyCallPrefController(Context context, String str) {
        super(context, str);
    }

    public static boolean isGestureAvailable(Context context) {
        return TriplePowerPressEmergencyCallUtil.isTriplePressEmergencyCallAvailable(context);
    }

    public CharSequence getSummary() {
        int i;
        Context context = this.mContext;
        if (isChecked()) {
            i = C1992R$string.triple_press_emergency_call_on;
        } else {
            i = C1992R$string.triple_press_emergency_call_off;
        }
        return context.getText(i);
    }

    public int getAvailabilityStatus() {
        return isGestureAvailable(this.mContext) ? 0 : 3;
    }

    public boolean isChecked() {
        return isTripleTapEmergencyCallEnabled(this.mContext);
    }

    public boolean setChecked(boolean z) {
        return TriplePowerPressEmergencyCallUtil.changeEmergencyCallEnablingState(this.mContext, z);
    }

    public static boolean isTripleTapEmergencyCallEnabled(Context context) {
        return TriplePowerPressEmergencyCallUtil.isTripleTapEmergencyCallEnabled(context);
    }
}

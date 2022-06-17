package com.motorola.settings.gestures;

import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;

public class TripleTapPowerEmergencyCallSettings extends DashboardFragment {
    public String getLogTag() {
        return "TripleTapPowerEmergencyCallSettings";
    }

    public int getMetricsCategory() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.triple_tap_power_emergency_call_setting;
    }
}

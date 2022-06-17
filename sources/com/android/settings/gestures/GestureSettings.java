package com.android.settings.gestures;

import android.content.Context;
import android.hardware.display.AmbientDisplayConfiguration;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.motorola.settings.gestures.DoublePressPowerToLaunchAppPreferenceController;
import com.motorola.settings.gestures.TripleTapPowerEmergencyCallPrefController;

public class GestureSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.gestures);
    private AmbientDisplayConfiguration mAmbientDisplayConfig;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "GestureSettings";
    }

    public int getMetricsCategory() {
        return 459;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.gestures;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        ((AssistGestureSettingsPreferenceController) use(AssistGestureSettingsPreferenceController.class)).setAssistOnly(false);
        ((PickupGesturePreferenceController) use(PickupGesturePreferenceController.class)).setConfig(getConfig(context));
        ((DoubleTapScreenPreferenceController) use(DoubleTapScreenPreferenceController.class)).setConfig(getConfig(context));
        use(DoublePressPowerToLaunchAppPreferenceController.class);
        addPreferenceController(new TripleTapPowerEmergencyCallPrefController(context, TripleTapPowerEmergencyCallPrefController.PREF_KEY));
    }

    private AmbientDisplayConfiguration getConfig(Context context) {
        if (this.mAmbientDisplayConfig == null) {
            this.mAmbientDisplayConfig = new AmbientDisplayConfiguration(context);
        }
        return this.mAmbientDisplayConfig;
    }
}

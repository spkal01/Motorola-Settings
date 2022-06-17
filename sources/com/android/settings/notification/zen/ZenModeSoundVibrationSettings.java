package com.android.settings.notification.zen;

import android.content.Context;
import com.android.settings.C1994R$xml;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;

public class ZenModeSoundVibrationSettings extends ZenModeSettingsBase {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.zen_mode_sound_vibration_settings) {
        public List<String> getNonIndexableKeys(Context context) {
            return super.getNonIndexableKeys(context);
        }

        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ZenModeSoundVibrationSettings.buildPreferenceControllers(context, (Lifecycle) null);
        }
    };

    public int getMetricsCategory() {
        return 141;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ZenModeAlarmsPreferenceController(context, lifecycle, "zen_mode_alarms"));
        arrayList.add(new ZenModeMediaPreferenceController(context, lifecycle));
        arrayList.add(new ZenModeSystemPreferenceController(context, lifecycle));
        arrayList.add(new ZenModeRemindersPreferenceController(context, lifecycle));
        arrayList.add(new ZenModeEventsPreferenceController(context, lifecycle));
        return arrayList;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.zen_mode_sound_vibration_settings;
    }
}

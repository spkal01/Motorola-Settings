package com.android.settings;

import android.content.Context;
import android.os.Bundle;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.display.BrightnessLevelPreferenceController;
import com.android.settings.display.CameraGesturePreferenceController;
import com.android.settings.display.LiftToWakePreferenceController;
import com.android.settings.display.ScreenSaverPreferenceController;
import com.android.settings.display.ShowOperatorNamePreferenceController;
import com.android.settings.display.TapToWakePreferenceController;
import com.android.settings.display.ThemePreferenceController;
import com.android.settings.display.VrDisplayPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.motorola.settings.utils.DisplayUtils;
import java.util.ArrayList;
import java.util.List;

public class DisplaySettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.display_settings) {
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return DisplaySettings.buildPreferenceControllers(context, (Lifecycle) null);
        }
    };

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "DisplaySettings";
    }

    public int getMetricsCategory() {
        return 46;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.display_settings;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public void onResume() {
        super.onResume();
        if (DisplayUtils.isDesktopMode(getActivity())) {
            getPreferenceScreen().setEnabled(false);
        }
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    public int getHelpResource() {
        return C1992R$string.help_uri_display;
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new CameraGesturePreferenceController(context));
        arrayList.add(new LiftToWakePreferenceController(context));
        arrayList.add(new ScreenSaverPreferenceController(context));
        arrayList.add(new TapToWakePreferenceController(context));
        arrayList.add(new VrDisplayPreferenceController(context));
        arrayList.add(new ShowOperatorNamePreferenceController(context));
        arrayList.add(new ThemePreferenceController(context));
        arrayList.add(new BrightnessLevelPreferenceController(context, lifecycle));
        return arrayList;
    }
}

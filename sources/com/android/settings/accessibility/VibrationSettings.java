package com.android.settings.accessibility;

import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;

public class VibrationSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.accessibility_vibration_settings);

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "VibrationSettings";
    }

    public int getMetricsCategory() {
        return 1292;
    }

    public int getHelpResource() {
        return C1992R$string.help_uri_accessibility_vibration;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.accessibility_vibration_settings;
    }
}

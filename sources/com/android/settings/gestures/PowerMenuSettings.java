package com.android.settings.gestures;

import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;

public class PowerMenuSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.power_menu_settings);

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "PowerMenuSettings";
    }

    public int getMetricsCategory() {
        return 1843;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.power_menu_settings;
    }
}

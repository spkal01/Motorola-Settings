package com.android.settings.accessibility;

import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;

public class TapAssistanceFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.accessibility_tap_assistance);

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "TapAssistanceFragment";
    }

    public int getMetricsCategory() {
        return 1861;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.accessibility_tap_assistance;
    }
}

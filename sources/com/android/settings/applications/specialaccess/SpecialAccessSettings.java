package com.android.settings.applications.specialaccess;

import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;

public class SpecialAccessSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.special_access);

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "SpecialAccessSettings";
    }

    public int getMetricsCategory() {
        return 351;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.special_access;
    }
}

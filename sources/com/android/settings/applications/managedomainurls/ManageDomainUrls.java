package com.android.settings.applications.managedomainurls;

import android.content.Context;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;

public class ManageDomainUrls extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.manage_domain_url_settings);

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "ManageDomainUrls";
    }

    public int getMetricsCategory() {
        return 143;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        ((DomainAppPreferenceController) use(DomainAppPreferenceController.class)).setFragment(this);
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.manage_domain_url_settings;
    }
}

package com.android.settings.security.trustagent;

import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;

public class TrustAgentSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.trust_agent_settings);

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "TrustAgentSettings";
    }

    public int getMetricsCategory() {
        return 91;
    }

    public int getHelpResource() {
        return C1992R$string.help_url_trust_agent;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.trust_agent_settings;
    }
}

package com.android.settings.development.featureflags;

import android.content.Context;
import android.provider.SearchIndexableResource;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import java.util.ArrayList;
import java.util.List;

public class FeatureFlagsDashboard extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C1994R$xml.feature_flags_settings;
            arrayList.add(searchIndexableResource);
            return arrayList;
        }

        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            return DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context);
        }
    };

    public int getHelpResource() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "FeatureFlagsDashboard";
    }

    public int getMetricsCategory() {
        return 1217;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.feature_flags_settings;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
    }
}

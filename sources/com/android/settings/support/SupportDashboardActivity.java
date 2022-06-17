package com.android.settings.support;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.android.settings.C1980R$bool;
import com.android.settings.C1992R$string;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.overlay.SupportFeatureProvider;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.List;

public class SupportDashboardActivity extends Activity {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
            int i = C1992R$string.page_tab_title_support;
            searchIndexableRaw.title = context.getString(i);
            searchIndexableRaw.screenTitle = context.getString(i);
            searchIndexableRaw.summaryOn = context.getString(C1992R$string.support_summary);
            searchIndexableRaw.intentTargetPackage = context.getPackageName();
            searchIndexableRaw.intentTargetClass = SupportDashboardActivity.class.getName();
            searchIndexableRaw.intentAction = "android.intent.action.MAIN";
            searchIndexableRaw.key = "support_dashboard_activity";
            arrayList.add(searchIndexableRaw);
            return arrayList;
        }

        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!context.getResources().getBoolean(C1980R$bool.config_support_enabled)) {
                nonIndexableKeys.add("support_dashboard_activity");
            }
            return nonIndexableKeys;
        }
    };

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SupportFeatureProvider supportFeatureProvider = FeatureFactory.getFactory(this).getSupportFeatureProvider(this);
        if (supportFeatureProvider != null) {
            supportFeatureProvider.startSupport(this);
            finish();
        }
    }
}

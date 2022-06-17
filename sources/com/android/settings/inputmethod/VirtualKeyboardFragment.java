package com.android.settings.inputmethod;

import android.content.Context;
import android.provider.SearchIndexableResource;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import java.util.Arrays;
import java.util.List;

public final class VirtualKeyboardFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C1994R$xml.virtual_keyboard_settings;
            return Arrays.asList(new SearchIndexableResource[]{searchIndexableResource});
        }
    };

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "VirtualKeyboardFragment";
    }

    public int getMetricsCategory() {
        return 345;
    }

    public int getHelpResource() {
        return C1992R$string.help_url_virtual_keyboard;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.virtual_keyboard_settings;
    }
}

package com.android.settings.inputmethod;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import java.util.ArrayList;
import java.util.List;

public class UserDictionaryList extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C1994R$xml.user_dictionary_list_fragment;
            arrayList.add(searchIndexableResource);
            return arrayList;
        }
    };

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "UserDictionaryList";
    }

    public int getMetricsCategory() {
        return 61;
    }

    public void onAttach(Context context) {
        String str;
        String str2;
        super.onAttach(context);
        Intent intent = getActivity().getIntent();
        String str3 = null;
        if (intent == null) {
            str = null;
        } else {
            str = intent.getStringExtra("locale");
        }
        Bundle arguments = getArguments();
        if (arguments == null) {
            str2 = null;
        } else {
            str2 = arguments.getString("locale");
        }
        if (str2 != null) {
            str3 = str2;
        } else if (str != null) {
            str3 = str;
        }
        ((UserDictionaryListPreferenceController) use(UserDictionaryListPreferenceController.class)).setLocale(str3);
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.user_dictionary_list_fragment;
    }
}

package com.android.settings.network;

import android.content.Context;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.telephony.SubscriptionManager;
import com.android.settings.C1994R$xml;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class MobileNetworkListFragment extends DashboardFragment {
    static final String KEY_PREFERENCE_CATEGORY_DOWNLOADED_SIM = "provider_model_downloaded_sim_category";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            int i;
            ArrayList arrayList = new ArrayList();
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            if (Utils.isProviderModelEnabled(context)) {
                i = C1994R$xml.network_provider_sims_list;
            } else {
                i = C1994R$xml.mobile_network_list;
            }
            searchIndexableResource.xmlResId = i;
            arrayList.add(searchIndexableResource);
            return arrayList;
        }

        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            if (((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).getActiveSubscriptionIdList().length <= 1) {
                return false;
            }
            return ((UserManager) context.getSystemService(UserManager.class)).isAdminUser();
        }
    };

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "NetworkListFragment";
    }

    public int getMetricsCategory() {
        return 1627;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        if (Utils.isProviderModelEnabled(getContext())) {
            return C1994R$xml.network_provider_sims_list;
        }
        return C1994R$xml.mobile_network_list;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        if (Utils.isProviderModelEnabled(getContext())) {
            NetworkProviderSimsCategoryController networkProviderSimsCategoryController = new NetworkProviderSimsCategoryController(context, "provider_model_sim_category");
            networkProviderSimsCategoryController.init(getSettingsLifecycle());
            arrayList.add(networkProviderSimsCategoryController);
            NetworkProviderDownloadedSimsCategoryController networkProviderDownloadedSimsCategoryController = new NetworkProviderDownloadedSimsCategoryController(context, KEY_PREFERENCE_CATEGORY_DOWNLOADED_SIM);
            networkProviderDownloadedSimsCategoryController.init(getSettingsLifecycle());
            arrayList.add(networkProviderDownloadedSimsCategoryController);
        } else {
            arrayList.add(new MobileNetworkListController(getContext(), getLifecycle()));
        }
        return arrayList;
    }
}

package com.android.settings.fuelgauge;

import android.content.Context;
import android.provider.SearchIndexableResource;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SmartBatterySettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C1994R$xml.smart_battery_detail;
            return Arrays.asList(new SearchIndexableResource[]{searchIndexableResource});
        }

        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return SmartBatterySettings.buildPreferenceControllers(context, (SettingsActivity) null, (InstrumentedPreferenceFragment) null);
        }
    };

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "SmartBatterySettings";
    }

    public int getMetricsCategory() {
        return 1882;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.smart_battery_detail;
    }

    public int getHelpResource() {
        return C1992R$string.help_uri_smart_battery_settings;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, (SettingsActivity) getActivity(), this);
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, SettingsActivity settingsActivity, InstrumentedPreferenceFragment instrumentedPreferenceFragment) {
        ArrayList arrayList = new ArrayList();
        if (settingsActivity == null || instrumentedPreferenceFragment == null) {
            arrayList.add(new RestrictAppPreferenceController(context));
        } else {
            arrayList.add(new RestrictAppPreferenceController(instrumentedPreferenceFragment));
        }
        return arrayList;
    }
}

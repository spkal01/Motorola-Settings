package com.android.settings.emergency;

import android.content.Context;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.EmergencyBroadcastPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class EmergencyDashboardFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.emergency_settings);

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "EmergencyDashboard";
    }

    public int getMetricsCategory() {
        return 1859;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.emergency_settings;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new EmergencyBroadcastPreferenceController(context, "app_and_notif_cell_broadcast_settings"));
        return arrayList;
    }

    public int getHelpResource() {
        return C1992R$string.help_url_safety_emergency;
    }
}

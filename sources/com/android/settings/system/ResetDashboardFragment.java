package com.android.settings.system;

import android.content.Context;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.applications.manageapplications.ResetAppPrefPreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.network.EraseEuiccDataController;
import com.android.settings.network.NetworkResetPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.motorola.settings.resetsettings.DeviceSettingsUtils;
import com.motorola.settings.resetsettings.ResetDeviceSettingsPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class ResetDashboardFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.reset_dashboard_fragment) {
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ResetDashboardFragment.buildPreferenceControllers(context, (Lifecycle) null);
        }
    };

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "ResetDashboardFragment";
    }

    public int getMetricsCategory() {
        return 924;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.reset_dashboard_fragment;
    }

    public int getHelpResource() {
        return C1992R$string.help_url_reset;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        ((EraseEuiccDataController) use(EraseEuiccDataController.class)).setFragment(this);
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new NetworkResetPreferenceController(context));
        arrayList.add(new FactoryResetPreferenceController(context));
        arrayList.add(new ResetAppPrefPreferenceController(context, lifecycle));
        if (DeviceSettingsUtils.isVzwSIM(context)) {
            arrayList.add(new ResetDeviceSettingsPreferenceController(context));
        }
        return arrayList;
    }
}

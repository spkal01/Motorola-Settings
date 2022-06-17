package com.android.settings.wifi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.FeatureFlagUtils;
import com.android.settings.C1980R$bool;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.wifi.p2p.WifiP2pPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.motorola.settings.wifi.WapiCertManagerPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class ConfigureWifiSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.wifi_configure_settings) {
        /* access modifiers changed from: protected */
        public boolean isPageSearchEnabled(Context context) {
            return context.getResources().getBoolean(C1980R$bool.config_show_wifi_settings);
        }
    };
    private WifiWakeupPreferenceController mWifiWakeupPreferenceController;

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "ConfigureWifiSettings";
    }

    public int getMetricsCategory() {
        return 338;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (FeatureFlagUtils.isEnabled(getContext(), "settings_provider_model")) {
            getActivity().setTitle(C1992R$string.network_and_internet_preferences_title);
        }
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.wifi_configure_settings;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        WifiManager wifiManager = (WifiManager) getSystemService("wifi");
        ArrayList arrayList = new ArrayList();
        arrayList.add(new WifiP2pPreferenceController(context, getSettingsLifecycle(), wifiManager));
        arrayList.add(new WapiCertManagerPreferenceController(context, wifiManager));
        return arrayList;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        WifiWakeupPreferenceController wifiWakeupPreferenceController = (WifiWakeupPreferenceController) use(WifiWakeupPreferenceController.class);
        this.mWifiWakeupPreferenceController = wifiWakeupPreferenceController;
        wifiWakeupPreferenceController.setFragment(this);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 600) {
            this.mWifiWakeupPreferenceController.onActivityResult(i, i2);
        } else {
            super.onActivityResult(i, i2, intent);
        }
    }
}

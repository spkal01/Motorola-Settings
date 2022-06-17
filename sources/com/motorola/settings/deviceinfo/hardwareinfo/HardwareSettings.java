package com.motorola.settings.deviceinfo.hardwareinfo;

import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;

public class HardwareSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.hardware_settings);

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "HardwareSettings";
    }

    public int getMetricsCategory() {
        return 862;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.hardware_settings;
    }

    public int getHelpResource() {
        return C1992R$string.help_url_hardware_settings;
    }
}

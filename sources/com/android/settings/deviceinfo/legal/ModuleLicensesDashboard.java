package com.android.settings.deviceinfo.legal;

import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;

public class ModuleLicensesDashboard extends DashboardFragment {
    public int getHelpResource() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "ModuleLicensesDashboard";
    }

    public int getMetricsCategory() {
        return 1746;
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.module_licenses;
    }
}

package com.android.settings.wifi;

import android.os.Bundle;
import com.android.settings.C1994R$xml;
import com.android.settings.SettingsPreferenceFragment;

public class WifiInfo extends SettingsPreferenceFragment {
    public int getMetricsCategory() {
        return 89;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C1994R$xml.testing_wifi_settings);
    }
}

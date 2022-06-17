package com.android.settings;

import android.os.Bundle;
import android.os.UserManager;
import androidx.preference.PreferenceScreen;
import com.android.settings.dashboard.DashboardFragment;

public class TestingSettings extends DashboardFragment {
    public static final String TAG = TestingSettings.class.getSimpleName();

    public int getMetricsCategory() {
        return 89;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!UserManager.get(getContext()).isAdminUser()) {
            getPreferenceScreen().removePreference((PreferenceScreen) findPreference("radio_info_settings"));
        }
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.testing_settings;
    }

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return TAG;
    }
}

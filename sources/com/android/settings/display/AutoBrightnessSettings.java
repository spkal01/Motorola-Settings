package com.android.settings.display;

import android.os.Bundle;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;

public class AutoBrightnessSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.auto_brightness_detail);

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "AutoBrightnessSettings";
    }

    public int getMetricsCategory() {
        return 1381;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.auto_brightness_detail;
    }

    public int getHelpResource() {
        return C1992R$string.help_url_auto_brightness;
    }
}

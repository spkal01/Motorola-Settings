package com.android.settings.gestures;

import android.content.Context;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;

public class DoubleTapPowerSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.double_tap_power_settings);

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "DoubleTapPower";
    }

    public int getMetricsCategory() {
        return 752;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        FeatureFactory.getFactory(context).getSuggestionFeatureProvider(context).getSharedPrefs(context).edit().putBoolean("pref_double_tap_power_suggestion_complete", true).apply();
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.double_tap_power_settings;
    }
}

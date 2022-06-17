package com.android.settings.gestures;

import android.content.Context;
import android.hardware.display.AmbientDisplayConfiguration;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;

public class PickupGestureSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C1994R$xml.pick_up_gesture_settings);

    /* access modifiers changed from: protected */
    public String getLogTag() {
        return "PickupGestureSettings";
    }

    public int getMetricsCategory() {
        return 753;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        FeatureFactory.getFactory(context).getSuggestionFeatureProvider(context).getSharedPrefs(context).edit().putBoolean("pref_pickup_gesture_suggestion_complete", true).apply();
        ((PickupGesturePreferenceController) use(PickupGesturePreferenceController.class)).setConfig(new AmbientDisplayConfiguration(context));
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.pick_up_gesture_settings;
    }

    public int getHelpResource() {
        return C1992R$string.help_url_pickup_gesture;
    }
}

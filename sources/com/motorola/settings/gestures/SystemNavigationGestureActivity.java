package com.motorola.settings.gestures;

import android.content.Context;
import com.android.settings.C1980R$bool;
import com.android.settings.SubSettings;
import com.android.settings.overlay.FeatureFactory;

public class SystemNavigationGestureActivity extends SubSettings {
    public static boolean isSuggestionComplete(Context context) {
        if (!context.getResources().getBoolean(C1980R$bool.config_system_navigation_suggestion)) {
            return true;
        }
        return FeatureFactory.getFactory(context).getSuggestionFeatureProvider(context).getSharedPrefs(context).getBoolean("pref_system_navigation_suggestion_complete", false);
    }
}

package com.motorola.settings.dashboard.suggestions;

import android.content.ComponentName;
import android.content.Context;
import com.android.settings.dashboard.suggestions.SuggestionFeatureProviderImpl;
import com.motorola.settings.gestures.SystemNavigationGestureActivity;

public class ExtendedSuggestionFeatureProviderImpl extends SuggestionFeatureProviderImpl {
    public ExtendedSuggestionFeatureProviderImpl(Context context) {
        super(context);
    }

    public boolean isSuggestionComplete(Context context, ComponentName componentName) {
        if (SystemNavigationGestureActivity.class.getName().equals(componentName.getClassName())) {
            return SystemNavigationGestureActivity.isSuggestionComplete(context);
        }
        return super.isSuggestionComplete(context, componentName);
    }
}

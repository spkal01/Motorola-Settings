package com.motorola.settings.display.edge;

import android.app.Activity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toolbar;
import com.android.settings.C1982R$dimen;
import com.android.settings.C1985R$id;
import com.android.settings.C1993R$style;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.motorola.settingslib.displayutils.EndlessLayoutMixin;

public class SettingsBaseEndlessLayoutMixin extends EndlessLayoutMixin {
    public static void applyTheme(Activity activity) {
        if (activity != null && EndlessLayoutMixin.isEndlessMixinAvailable(activity)) {
            activity.getTheme().applyStyle(C1993R$style.EdgeSettingsTheme, true);
        }
    }

    public static LayoutInflater applyThemeToFragment(LayoutInflater layoutInflater) {
        if (!EndlessLayoutMixin.isEndlessMixinAvailable(layoutInflater.getContext())) {
            return layoutInflater;
        }
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(layoutInflater.getContext(), layoutInflater.getContext().getTheme());
        contextThemeWrapper.getTheme().applyStyle(C1993R$style.EdgePreferenceTheme, true);
        return layoutInflater.cloneInContext(contextThemeWrapper);
    }

    public SettingsBaseEndlessLayoutMixin(Activity activity) {
        super(activity);
    }

    public void onApplyEdgeLayout(Activity activity) {
        int dimensionPixelOffset = activity.getResources().getDimensionPixelOffset(C1982R$dimen.edge_preferred_padding);
        int dimensionPixelOffset2 = activity.getResources().getDimensionPixelOffset(C1982R$dimen.edge_short_preferred_padding);
        ((ViewGroup) activity.findViewById(C1985R$id.content_parent)).setOnApplyWindowInsetsListener(SettingsBaseEndlessLayoutMixin$$ExternalSyntheticLambda2.INSTANCE);
        Toolbar toolbar = (Toolbar) activity.findViewById(C1985R$id.action_bar);
        if (toolbar != null) {
            toolbar.setOnApplyWindowInsetsListener(new SettingsBaseEndlessLayoutMixin$$ExternalSyntheticLambda0(toolbar, dimensionPixelOffset2));
        }
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) activity.findViewById(C1985R$id.collapsing_toolbar);
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setExpandedTitleMarginStart(dimensionPixelOffset);
        }
        LinearLayout linearLayout = (LinearLayout) activity.findViewById(C1985R$id.switch_bar);
        if (linearLayout != null) {
            linearLayout.setOnApplyWindowInsetsListener(SettingsBaseEndlessLayoutMixin$$ExternalSyntheticLambda1.INSTANCE);
        }
    }
}

package com.motorola.settings.display.edge;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.C1982R$dimen;
import com.android.settings.C1985R$id;
import com.motorola.settingslib.displayutils.EndlessLayoutMixin;

public class SettingsHomeEndlessLayoutMixin extends EndlessLayoutMixin {
    public SettingsHomeEndlessLayoutMixin(Activity activity) {
        super(activity);
    }

    public void onApplyEdgeLayout(Activity activity) {
        ((ViewGroup) activity.findViewById(C1985R$id.settings_homepage_container)).setOnApplyWindowInsetsListener(SettingsHomeEndlessLayoutMixin$$ExternalSyntheticLambda0.INSTANCE);
        int dimensionPixelOffset = activity.getResources().getDimensionPixelOffset(C1982R$dimen.edge_preferred_padding);
        TextView textView = (TextView) activity.findViewById(C1985R$id.homepage_title);
        if (textView != null) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
            layoutParams.leftMargin = dimensionPixelOffset;
            layoutParams.rightMargin = dimensionPixelOffset;
            textView.setLayoutParams(layoutParams);
        }
        ViewGroup viewGroup = (ViewGroup) activity.findViewById(C1985R$id.search_bar_container);
        if (viewGroup != null && (viewGroup.getLayoutParams() instanceof LinearLayout.LayoutParams)) {
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) viewGroup.getLayoutParams();
            layoutParams2.leftMargin = dimensionPixelOffset;
            layoutParams2.rightMargin = dimensionPixelOffset;
        }
        FrameLayout frameLayout = (FrameLayout) activity.findViewById(C1985R$id.contextual_cards_content);
        if (frameLayout != null) {
            int dimensionPixelOffset2 = activity.getResources().getDimensionPixelOffset(C1982R$dimen.contextual_card_side_margin);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
            int i = dimensionPixelOffset - dimensionPixelOffset2;
            marginLayoutParams.leftMargin = i;
            marginLayoutParams.rightMargin = i;
        }
    }
}

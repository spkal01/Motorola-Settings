package com.motorola.settings.display.edge;

import android.app.Activity;
import android.widget.LinearLayout;
import com.android.settings.C1985R$id;
import com.motorola.settingslib.displayutils.EndlessLayoutMixin;

public class SettingsNetworkEndlessLayoutMixin extends EndlessLayoutMixin {
    public void onApplyEdgeLayout(Activity activity) {
        LinearLayout linearLayout = (LinearLayout) activity.findViewById(C1985R$id.main_switch_bar);
        if (linearLayout != null) {
            linearLayout.setOnApplyWindowInsetsListener(SettingsNetworkEndlessLayoutMixin$$ExternalSyntheticLambda0.INSTANCE);
        }
    }
}

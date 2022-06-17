package com.motorola.settings.display.edge;

import android.view.View;
import android.view.WindowInsets;

public final /* synthetic */ class SettingsNetworkEndlessLayoutMixin$$ExternalSyntheticLambda0 implements View.OnApplyWindowInsetsListener {
    public static final /* synthetic */ SettingsNetworkEndlessLayoutMixin$$ExternalSyntheticLambda0 INSTANCE = new SettingsNetworkEndlessLayoutMixin$$ExternalSyntheticLambda0();

    private /* synthetic */ SettingsNetworkEndlessLayoutMixin$$ExternalSyntheticLambda0() {
    }

    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return view.setPadding(windowInsets.getStableInsetLeft(), view.getPaddingTop(), windowInsets.getStableInsetRight(), view.getPaddingBottom());
    }
}

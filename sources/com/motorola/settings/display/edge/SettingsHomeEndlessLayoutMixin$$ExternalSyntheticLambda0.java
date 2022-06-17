package com.motorola.settings.display.edge;

import android.view.View;
import android.view.WindowInsets;

public final /* synthetic */ class SettingsHomeEndlessLayoutMixin$$ExternalSyntheticLambda0 implements View.OnApplyWindowInsetsListener {
    public static final /* synthetic */ SettingsHomeEndlessLayoutMixin$$ExternalSyntheticLambda0 INSTANCE = new SettingsHomeEndlessLayoutMixin$$ExternalSyntheticLambda0();

    private /* synthetic */ SettingsHomeEndlessLayoutMixin$$ExternalSyntheticLambda0() {
    }

    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return view.setPadding(0, windowInsets.getStableInsetTop(), 0, windowInsets.getStableInsetBottom());
    }
}

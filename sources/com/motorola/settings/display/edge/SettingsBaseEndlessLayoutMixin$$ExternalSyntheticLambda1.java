package com.motorola.settings.display.edge;

import android.view.View;
import android.view.WindowInsets;

public final /* synthetic */ class SettingsBaseEndlessLayoutMixin$$ExternalSyntheticLambda1 implements View.OnApplyWindowInsetsListener {
    public static final /* synthetic */ SettingsBaseEndlessLayoutMixin$$ExternalSyntheticLambda1 INSTANCE = new SettingsBaseEndlessLayoutMixin$$ExternalSyntheticLambda1();

    private /* synthetic */ SettingsBaseEndlessLayoutMixin$$ExternalSyntheticLambda1() {
    }

    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return view.setPadding(windowInsets.getStableInsetLeft(), view.getPaddingTop(), windowInsets.getStableInsetRight(), view.getPaddingBottom());
    }
}

package com.motorola.settings.display.edge;

import android.view.View;
import android.view.WindowInsets;
import android.widget.Toolbar;

public final /* synthetic */ class SettingsBaseEndlessLayoutMixin$$ExternalSyntheticLambda0 implements View.OnApplyWindowInsetsListener {
    public final /* synthetic */ Toolbar f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ SettingsBaseEndlessLayoutMixin$$ExternalSyntheticLambda0(Toolbar toolbar, int i) {
        this.f$0 = toolbar;
        this.f$1 = i;
    }

    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return this.f$0.setPadding(this.f$1, view.getPaddingTop(), this.f$1, view.getPaddingBottom());
    }
}

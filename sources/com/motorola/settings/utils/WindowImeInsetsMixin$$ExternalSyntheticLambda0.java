package com.motorola.settings.utils;

import android.view.View;
import android.view.WindowInsets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;

public final /* synthetic */ class WindowImeInsetsMixin$$ExternalSyntheticLambda0 implements OnApplyWindowInsetsListener {
    public final /* synthetic */ View f$0;

    public /* synthetic */ WindowImeInsetsMixin$$ExternalSyntheticLambda0(View view) {
        this.f$0 = view;
    }

    public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
        return this.f$0.setPadding(this.f$0.getPaddingLeft(), this.f$0.getPaddingTop(), this.f$0.getPaddingRight(), windowInsetsCompat.getInsets(WindowInsets.Type.ime()).bottom);
    }
}

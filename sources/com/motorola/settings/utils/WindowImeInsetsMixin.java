package com.motorola.settings.utils;

import android.view.View;
import androidx.core.view.ViewCompat;

public class WindowImeInsetsMixin {
    public static void setOnApplyWindowInsetsListener(View view) {
        if (view != null) {
            ViewCompat.setOnApplyWindowInsetsListener(view, new WindowImeInsetsMixin$$ExternalSyntheticLambda0(view));
        }
    }
}

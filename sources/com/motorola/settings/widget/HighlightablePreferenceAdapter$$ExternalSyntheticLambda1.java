package com.motorola.settings.widget;

import android.animation.ValueAnimator;
import android.view.View;

public final /* synthetic */ class HighlightablePreferenceAdapter$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ View f$0;

    public /* synthetic */ HighlightablePreferenceAdapter$$ExternalSyntheticLambda1(View view) {
        this.f$0 = view;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.setBackgroundColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }
}

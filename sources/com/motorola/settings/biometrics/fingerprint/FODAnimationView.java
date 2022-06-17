package com.motorola.settings.biometrics.fingerprint;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;

public class FODAnimationView extends FrameLayout {
    private static final int[] ANIMATION_STYLES = {17304873, 17304875, 17304874};
    private static final String TAG = FODAnimationView.class.getSimpleName();
    /* access modifiers changed from: private */
    public AnimatedVectorDrawable mAnimation;
    private Animatable2.AnimationCallback mAnimationCallback = new Animatable2.AnimationCallback() {
        public void onAnimationEnd(Drawable drawable) {
            super.onAnimationEnd(drawable);
            if (FODAnimationView.this.mAnimationView != null && FODAnimationView.this.mAnimation != null) {
                FODAnimationView.this.mAnimationView.post(new FODAnimationView$1$$ExternalSyntheticLambda0(this));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onAnimationEnd$0() {
            FODAnimationView.this.mAnimation.start();
        }
    };
    /* access modifiers changed from: private */
    public final ImageView mAnimationView;

    public FODAnimationView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        FrameLayout.inflate(context, C1987R$layout.fingerprint_fod_animation_view, this);
        this.mAnimationView = (ImageView) findViewById(C1985R$id.animation);
    }

    public void setStyle(int i) {
        if (this.mAnimationView != null) {
            AnimatedVectorDrawable animatedVectorDrawable = this.mAnimation;
            if (animatedVectorDrawable != null) {
                animatedVectorDrawable.clearAnimationCallbacks();
            }
            this.mAnimation = null;
            this.mAnimationView.setImageDrawable((Drawable) null);
            try {
                Drawable drawable = getContext().getDrawable(ANIMATION_STYLES[i]);
                if (drawable instanceof AnimatedVectorDrawable) {
                    this.mAnimationView.setImageDrawable(drawable);
                    AnimatedVectorDrawable animatedVectorDrawable2 = (AnimatedVectorDrawable) drawable;
                    this.mAnimation = animatedVectorDrawable2;
                    animatedVectorDrawable2.registerAnimationCallback(this.mAnimationCallback);
                    this.mAnimation.start();
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
}

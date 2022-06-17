package com.motorola.settings.display.edge;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;

public class EdgeSensitivityIndicatorView extends LinearLayout {
    public static final boolean DEBUG = (!"user".equals(Build.TYPE));
    private ViewGroup mLayout;
    private ObjectAnimator mLeftAnimator;
    /* access modifiers changed from: private */
    public ImageView mLeftIndicator;
    private ObjectAnimator mRightAnimator;
    /* access modifiers changed from: private */
    public ImageView mRightIndicator;

    public EdgeSensitivityIndicatorView(Context context) {
        super(context);
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(C1987R$layout.edge_sensitivity_indicator_container, this, false);
        this.mLayout = viewGroup;
        if (viewGroup != null) {
            addView(viewGroup);
            this.mLeftIndicator = (ImageView) this.mLayout.findViewById(C1985R$id.indicator_left);
            this.mRightIndicator = (ImageView) this.mLayout.findViewById(C1985R$id.indicator_right);
            int systemUiVisibility = getSystemUiVisibility() | 2048 | 256 | 512 | 1024 | 2 | 4;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(new int[]{16844140, 16844000});
            systemUiVisibility = obtainStyledAttributes.getBoolean(0, false) ? systemUiVisibility | 16 : systemUiVisibility;
            systemUiVisibility = obtainStyledAttributes.getBoolean(1, false) ? systemUiVisibility | 8192 : systemUiVisibility;
            obtainStyledAttributes.recycle();
            ImageView imageView = this.mLeftIndicator;
            ObjectAnimator duration = ObjectAnimator.ofFloat(imageView, "translationX", new float[]{(float) (imageView.getWidth() / 4), 0.0f}).setDuration(700);
            this.mLeftAnimator = duration;
            duration.addListener(new Animator.AnimatorListener() {
                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                }

                public void onAnimationEnd(Animator animator) {
                    EdgeSensitivityIndicatorView.this.mLeftIndicator.setVisibility(8);
                }

                public void onAnimationCancel(Animator animator) {
                    EdgeSensitivityIndicatorView.this.mLeftIndicator.setVisibility(8);
                }
            });
            ImageView imageView2 = this.mRightIndicator;
            ObjectAnimator duration2 = ObjectAnimator.ofFloat(imageView2, "translationX", new float[]{0.0f, (float) imageView2.getWidth()}).setDuration(700);
            this.mRightAnimator = duration2;
            duration2.addListener(new Animator.AnimatorListener() {
                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                }

                public void onAnimationEnd(Animator animator) {
                    EdgeSensitivityIndicatorView.this.mRightIndicator.setVisibility(8);
                }

                public void onAnimationCancel(Animator animator) {
                    EdgeSensitivityIndicatorView.this.mRightIndicator.setVisibility(8);
                }
            });
            setSystemUiVisibility(systemUiVisibility);
        }
    }

    public void showIndicator(int i, int i2) {
        ImageView imageView;
        ObjectAnimator objectAnimator;
        if (i < 200) {
            imageView = this.mLeftIndicator;
            if (DEBUG) {
                Log.d("EdgeSensitivityIndicatorView", "setIndicator left");
            }
            objectAnimator = this.mLeftAnimator;
        } else {
            imageView = this.mRightIndicator;
            objectAnimator = this.mRightAnimator;
            if (DEBUG) {
                Log.d("EdgeSensitivityIndicatorView", "setIndicator right");
            }
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.setMargins(0, i2 - 200, 0, 0);
        imageView.setLayoutParams(layoutParams);
        imageView.setVisibility(0);
        if (objectAnimator.isStarted()) {
            objectAnimator.end();
        }
        objectAnimator.start();
    }

    public void hideIndicators() {
        this.mLeftIndicator.setVisibility(8);
        this.mRightIndicator.setVisibility(8);
    }

    public WindowManager.LayoutParams getLayoutParams(WindowManager.LayoutParams layoutParams) {
        WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams(2038, (layoutParams.flags & Integer.MIN_VALUE) | 16777240, -3);
        layoutParams2.setTitle("EdgeSensitivityIndicatorView");
        layoutParams2.layoutInDisplayCutoutMode = 3;
        layoutParams2.setFitInsetsTypes(0);
        layoutParams2.token = getContext().getActivityToken();
        return layoutParams2;
    }
}

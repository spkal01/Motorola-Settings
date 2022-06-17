package com.android.settings.widget;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class LoadingViewController {
    private final View mContentView;
    private final View mEmptyView;
    private final Handler mFgHandler;
    private final View mLoadingView;
    private Runnable mShowLoadingContainerRunnable;

    public LoadingViewController(View view, View view2) {
        this(view, view2, (View) null);
    }

    public LoadingViewController(View view, View view2, View view3) {
        this.mShowLoadingContainerRunnable = new Runnable() {
            public void run() {
                LoadingViewController.this.showLoadingView();
            }
        };
        this.mLoadingView = view;
        this.mContentView = view2;
        this.mEmptyView = view3;
        this.mFgHandler = new Handler(Looper.getMainLooper());
    }

    public void showContent(boolean z) {
        this.mFgHandler.removeCallbacks(this.mShowLoadingContainerRunnable);
        handleLoadingContainer(true, false, z);
    }

    public void showEmpty(boolean z) {
        if (this.mEmptyView != null) {
            this.mFgHandler.removeCallbacks(this.mShowLoadingContainerRunnable);
            handleLoadingContainer(false, true, z);
        }
    }

    public void showLoadingView() {
        handleLoadingContainer(false, false, false);
    }

    public void showLoadingViewDelayed() {
        this.mFgHandler.postDelayed(this.mShowLoadingContainerRunnable, 100);
    }

    private void handleLoadingContainer(boolean z, boolean z2, boolean z3) {
        handleLoadingContainer(this.mLoadingView, this.mContentView, this.mEmptyView, z, z2, z3);
    }

    public static void handleLoadingContainer(View view, View view2, boolean z, boolean z2) {
        setViewShown(view, !z, z2);
        setViewShown(view2, z, z2);
    }

    public static void handleLoadingContainer(View view, View view2, View view3, boolean z, boolean z2, boolean z3) {
        if (view3 != null) {
            setViewShown(view3, z2, z3);
        }
        setViewShown(view2, z, z3);
        setViewShown(view, !z && !z2, z3);
    }

    private static void setViewShown(final View view, boolean z, boolean z2) {
        int i = 0;
        if (z2) {
            Animation loadAnimation = AnimationUtils.loadAnimation(view.getContext(), z ? 17432576 : 17432577);
            if (z) {
                view.setVisibility(0);
            } else {
                loadAnimation.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationStart(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        view.setVisibility(4);
                    }
                });
            }
            view.startAnimation(loadAnimation);
            return;
        }
        view.clearAnimation();
        if (!z) {
            i = 4;
        }
        view.setVisibility(i);
    }
}

package com.motorola.settingslib.displayutils;

import android.app.Activity;
import android.content.Context;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import motorola.core_services.misc.WaterfallManager;

public class EndlessLayoutMixin implements LifecycleObserver {
    private final Activity mActivity;

    public void onApplyEdgeLayout(Activity activity) {
    }

    public EndlessLayoutMixin(Activity activity) {
        this.mActivity = activity;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public final void onCreate() {
        Activity activity = this.mActivity;
        if (activity != null && isEndlessMixinAvailable(activity)) {
            this.mActivity.getWindow().getAttributes().layoutInDisplayCutoutMode = 3;
            onApplyEdgeLayout(this.mActivity);
        }
    }

    public static boolean isEndlessMixinAvailable(Context context) {
        if (!WaterfallManager.hasWaterfallDisplay(context) || context.getResources().getConfiguration().orientation != 1) {
            return false;
        }
        return true;
    }
}

package com.motorola.settings.biometrics.fingerprint;

import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.motorola.android.provider.MotorolaSettings;

public class FODAnimationViewController implements LifecycleObserver {
    private ContentObserver mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        public void onChange(boolean z) {
            super.onChange(z);
            FODAnimationViewController.this.updateAnimationView();
        }
    };
    private final FODAnimationView mView;

    public FODAnimationViewController(FODAnimationView fODAnimationView) {
        this.mView = fODAnimationView;
    }

    public void init(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        FODAnimationView fODAnimationView = this.mView;
        if (fODAnimationView != null) {
            fODAnimationView.getContext().getContentResolver().registerContentObserver(MotorolaSettings.Secure.getUriFor("fod_animation_style"), true, this.mContentObserver);
            updateAnimationView();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        FODAnimationView fODAnimationView = this.mView;
        if (fODAnimationView != null) {
            fODAnimationView.getContext().getContentResolver().unregisterContentObserver(this.mContentObserver);
        }
    }

    /* access modifiers changed from: private */
    public void updateAnimationView() {
        int fingerprintAnimationStyle = FingerprintUtils.getFingerprintAnimationStyle(this.mView.getContext());
        FODAnimationView fODAnimationView = this.mView;
        if (fODAnimationView != null) {
            fODAnimationView.setStyle(fingerprintAnimationStyle);
        }
    }
}

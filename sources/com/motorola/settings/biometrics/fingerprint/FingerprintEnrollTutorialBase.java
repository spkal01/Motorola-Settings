package com.motorola.settings.biometrics.fingerprint;

import android.os.Bundle;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.C1985R$id;
import com.android.settings.C1987R$layout;
import com.android.settings.biometrics.fingerprint.FingerprintEnrollBase;

public abstract class FingerprintEnrollTutorialBase extends FingerprintEnrollBase {
    private LottieAnimationView mAnimation;

    /* access modifiers changed from: protected */
    public abstract int getAnimationResId();

    /* access modifiers changed from: protected */
    public abstract int getMsgResId();

    /* access modifiers changed from: protected */
    public abstract int getTitleResId();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C1987R$layout.fingerprint_enroll_tutorial_base);
        setHeaderText(getTitleResId());
        setDescriptionText(getMsgResId());
        LottieAnimationView lottieAnimationView = (LottieAnimationView) findViewById(C1985R$id.fingerprint_sensor_location_animation);
        this.mAnimation = lottieAnimationView;
        lottieAnimationView.setAnimation(getAnimationResId());
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
    }
}

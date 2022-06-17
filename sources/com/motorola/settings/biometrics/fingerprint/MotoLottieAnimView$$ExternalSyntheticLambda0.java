package com.motorola.settings.biometrics.fingerprint;

import android.util.Log;
import com.airbnb.lottie.LottieListener;

public final /* synthetic */ class MotoLottieAnimView$$ExternalSyntheticLambda0 implements LottieListener {
    public static final /* synthetic */ MotoLottieAnimView$$ExternalSyntheticLambda0 INSTANCE = new MotoLottieAnimView$$ExternalSyntheticLambda0();

    private /* synthetic */ MotoLottieAnimView$$ExternalSyntheticLambda0() {
    }

    public final void onResult(Object obj) {
        Log.w("MotoLottieAnimView", "Invalid illustration resource id: ", (Throwable) obj);
    }
}

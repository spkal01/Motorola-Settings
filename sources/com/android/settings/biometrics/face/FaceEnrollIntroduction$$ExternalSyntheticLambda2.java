package com.android.settings.biometrics.face;

import android.content.Intent;
import android.hardware.face.FaceManager;

public final /* synthetic */ class FaceEnrollIntroduction$$ExternalSyntheticLambda2 implements FaceManager.GenerateChallengeCallback {
    public final /* synthetic */ FaceEnrollIntroduction f$0;
    public final /* synthetic */ Intent f$1;

    public /* synthetic */ FaceEnrollIntroduction$$ExternalSyntheticLambda2(FaceEnrollIntroduction faceEnrollIntroduction, Intent intent) {
        this.f$0 = faceEnrollIntroduction;
        this.f$1 = intent;
    }

    public final void onGenerateChallengeResult(int i, int i2, long j) {
        this.f$0.lambda$checkTokenAndOpenMotoFaceUnlock$1(this.f$1, i, i2, j);
    }
}

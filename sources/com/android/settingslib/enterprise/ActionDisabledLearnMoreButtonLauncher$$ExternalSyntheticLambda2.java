package com.android.settingslib.enterprise;

import android.content.Context;
import android.os.UserHandle;

public final /* synthetic */ class ActionDisabledLearnMoreButtonLauncher$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ ActionDisabledLearnMoreButtonLauncher f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ UserHandle f$3;

    public /* synthetic */ ActionDisabledLearnMoreButtonLauncher$$ExternalSyntheticLambda2(ActionDisabledLearnMoreButtonLauncher actionDisabledLearnMoreButtonLauncher, Context context, String str, UserHandle userHandle) {
        this.f$0 = actionDisabledLearnMoreButtonLauncher;
        this.f$1 = context;
        this.f$2 = str;
        this.f$3 = userHandle;
    }

    public final void run() {
        this.f$0.lambda$setupLearnMoreButtonToLaunchHelpPage$2(this.f$1, this.f$2, this.f$3);
    }
}

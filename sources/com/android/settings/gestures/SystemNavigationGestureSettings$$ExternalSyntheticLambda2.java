package com.android.settings.gestures;

import android.content.Intent;
import android.view.View;

public final /* synthetic */ class SystemNavigationGestureSettings$$ExternalSyntheticLambda2 implements View.OnClickListener {
    public final /* synthetic */ SystemNavigationGestureSettings f$0;
    public final /* synthetic */ Intent f$1;

    public /* synthetic */ SystemNavigationGestureSettings$$ExternalSyntheticLambda2(SystemNavigationGestureSettings systemNavigationGestureSettings, Intent intent) {
        this.f$0 = systemNavigationGestureSettings;
        this.f$1 = intent;
    }

    public final void onClick(View view) {
        this.f$0.lambda$bindPreferenceAction$2(this.f$1, view);
    }
}

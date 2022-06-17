package com.android.settings.accessibility;

import android.content.Context;
import android.view.View;
import com.android.settings.core.SubSettingLauncher;

public final /* synthetic */ class AccessibilityDialogUtils$$ExternalSyntheticLambda1 implements View.OnClickListener {
    public final /* synthetic */ Context f$0;

    public /* synthetic */ AccessibilityDialogUtils$$ExternalSyntheticLambda1(Context context) {
        this.f$0 = context;
    }

    public final void onClick(View view) {
        new SubSettingLauncher(this.f$0).setDestination(AccessibilityButtonFragment.class.getName()).setSourceMetricsCategory(1873).launch();
    }
}

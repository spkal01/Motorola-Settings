package com.android.settings.accessibility;

import android.app.Dialog;
import android.view.View;
import com.android.settings.accessibility.AccessibilityDialogUtils;

public final /* synthetic */ class AccessibilityDialogUtils$$ExternalSyntheticLambda3 implements View.OnClickListener {
    public final /* synthetic */ AccessibilityDialogUtils.CustomButtonsClickListener f$0;
    public final /* synthetic */ Dialog f$1;

    public /* synthetic */ AccessibilityDialogUtils$$ExternalSyntheticLambda3(AccessibilityDialogUtils.CustomButtonsClickListener customButtonsClickListener, Dialog dialog) {
        this.f$0 = customButtonsClickListener;
        this.f$1 = dialog;
    }

    public final void onClick(View view) {
        AccessibilityDialogUtils.lambda$setCustomButtonsClickListener$2(this.f$0, this.f$1, view);
    }
}

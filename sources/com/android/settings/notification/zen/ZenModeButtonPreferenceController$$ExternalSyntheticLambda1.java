package com.android.settings.notification.zen;

import android.view.View;
import androidx.preference.Preference;

public final /* synthetic */ class ZenModeButtonPreferenceController$$ExternalSyntheticLambda1 implements View.OnClickListener {
    public final /* synthetic */ ZenModeButtonPreferenceController f$0;
    public final /* synthetic */ Preference f$1;

    public /* synthetic */ ZenModeButtonPreferenceController$$ExternalSyntheticLambda1(ZenModeButtonPreferenceController zenModeButtonPreferenceController, Preference preference) {
        this.f$0 = zenModeButtonPreferenceController;
        this.f$1 = preference;
    }

    public final void onClick(View view) {
        this.f$0.lambda$updateZenButtonOnClickListener$1(this.f$1, view);
    }
}

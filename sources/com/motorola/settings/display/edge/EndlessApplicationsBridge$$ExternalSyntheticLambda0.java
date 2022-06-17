package com.motorola.settings.display.edge;

import android.widget.CompoundButton;
import com.android.settingslib.applications.ApplicationsState;

public final /* synthetic */ class EndlessApplicationsBridge$$ExternalSyntheticLambda0 implements CompoundButton.OnCheckedChangeListener {
    public final /* synthetic */ EndlessApplicationsBridge f$0;
    public final /* synthetic */ ApplicationsState.AppEntry f$1;

    public /* synthetic */ EndlessApplicationsBridge$$ExternalSyntheticLambda0(EndlessApplicationsBridge endlessApplicationsBridge, ApplicationsState.AppEntry appEntry) {
        this.f$0 = endlessApplicationsBridge;
        this.f$1 = appEntry;
    }

    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        this.f$0.lambda$getSwitchOnCheckedListener$0(this.f$1, compoundButton, z);
    }
}

package com.motorola.settings.display.fullscreen;

import android.widget.CompoundButton;
import com.android.settingslib.applications.ApplicationsState;

public final /* synthetic */ class FullscreenAppsBridge$$ExternalSyntheticLambda0 implements CompoundButton.OnCheckedChangeListener {
    public final /* synthetic */ FullscreenAppsBridge f$0;
    public final /* synthetic */ ApplicationsState.AppEntry f$1;

    public /* synthetic */ FullscreenAppsBridge$$ExternalSyntheticLambda0(FullscreenAppsBridge fullscreenAppsBridge, ApplicationsState.AppEntry appEntry) {
        this.f$0 = fullscreenAppsBridge;
        this.f$1 = appEntry;
    }

    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        this.f$0.lambda$getSwitchOnCheckedListener$0(this.f$1, compoundButton, z);
    }
}

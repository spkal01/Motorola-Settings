package com.android.settings.applications;

import android.widget.CompoundButton;
import com.android.settingslib.applications.ApplicationsState;

public final /* synthetic */ class AppStateNotificationBridge$$ExternalSyntheticLambda0 implements CompoundButton.OnCheckedChangeListener {
    public final /* synthetic */ AppStateNotificationBridge f$0;
    public final /* synthetic */ ApplicationsState.AppEntry f$1;

    public /* synthetic */ AppStateNotificationBridge$$ExternalSyntheticLambda0(AppStateNotificationBridge appStateNotificationBridge, ApplicationsState.AppEntry appEntry) {
        this.f$0 = appStateNotificationBridge;
        this.f$1 = appEntry;
    }

    public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        this.f$0.lambda$getSwitchOnCheckedListener$0(this.f$1, compoundButton, z);
    }
}

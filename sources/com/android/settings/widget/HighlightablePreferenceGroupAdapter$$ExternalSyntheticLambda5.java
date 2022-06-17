package com.android.settings.widget;

import com.google.android.material.appbar.AppBarLayout;

public final /* synthetic */ class HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ AppBarLayout f$0;

    public /* synthetic */ HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda5(AppBarLayout appBarLayout) {
        this.f$0 = appBarLayout;
    }

    public final void run() {
        this.f$0.setExpanded(false, true);
    }
}

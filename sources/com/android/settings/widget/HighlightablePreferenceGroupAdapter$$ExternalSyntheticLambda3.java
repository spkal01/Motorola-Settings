package com.android.settings.widget;

import androidx.preference.PreferenceViewHolder;

public final /* synthetic */ class HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ HighlightablePreferenceGroupAdapter f$0;
    public final /* synthetic */ PreferenceViewHolder f$1;

    public /* synthetic */ HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda3(HighlightablePreferenceGroupAdapter highlightablePreferenceGroupAdapter, PreferenceViewHolder preferenceViewHolder) {
        this.f$0 = highlightablePreferenceGroupAdapter;
        this.f$1 = preferenceViewHolder;
    }

    public final void run() {
        this.f$0.lambda$requestRemoveHighlightDelayed$3(this.f$1);
    }
}

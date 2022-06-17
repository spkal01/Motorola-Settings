package com.motorola.settings.widget;

import androidx.recyclerview.widget.RecyclerView;

public final /* synthetic */ class HighlightablePreferenceAdapter$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ HighlightablePreferenceAdapter f$0;
    public final /* synthetic */ RecyclerView f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ HighlightablePreferenceAdapter$$ExternalSyntheticLambda4(HighlightablePreferenceAdapter highlightablePreferenceAdapter, RecyclerView recyclerView, int i) {
        this.f$0 = highlightablePreferenceAdapter;
        this.f$1 = recyclerView;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$requestHighlight$1(this.f$1, this.f$2);
    }
}

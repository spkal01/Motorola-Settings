package com.motorola.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.android.settings.widget.FloatingAppBarScrollingViewBehavior;

public class ManageApplicationsScrollingViewBehavior extends FloatingAppBarScrollingViewBehavior {
    /* access modifiers changed from: protected */
    public boolean shouldHeaderOverlapScrollingChild() {
        return false;
    }

    public ManageApplicationsScrollingViewBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}

package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.C1992R$string;
import com.android.settings.slices.SliceBackgroundWorker;

public class ToggleAutoclickFooterPreferenceController extends AccessibilityFooterPreferenceController {
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public ToggleAutoclickFooterPreferenceController(Context context, String str) {
        super(context, str);
    }

    /* access modifiers changed from: protected */
    public String getLearnMoreContentDescription() {
        return this.mContext.getString(C1992R$string.accessibility_autoclick_footer_learn_more_content_description);
    }

    /* access modifiers changed from: protected */
    public String getIntroductionTitle() {
        return this.mContext.getString(C1992R$string.accessibility_autoclick_about_title);
    }

    /* access modifiers changed from: protected */
    public int getHelpResource() {
        return C1992R$string.help_url_autoclick;
    }
}

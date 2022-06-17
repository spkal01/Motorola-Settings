package com.android.settings.accessibility;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.HelpUtils;

public class AccessibilityFooterPreferenceController extends BasePreferenceController {
    private int mHelpResource;
    private String mIntroductionTitle;
    private String mLearnMoreContentDescription;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public int getAvailabilityStatus() {
        return 0;
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

    public AccessibilityFooterPreferenceController(Context context, String str) {
        super(context, str);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        updateFooterPreferences((AccessibilityFooterPreference) preferenceScreen.findPreference(getPreferenceKey()));
    }

    public void setupHelpLink(int i, String str) {
        this.mHelpResource = i;
        this.mLearnMoreContentDescription = str;
    }

    /* access modifiers changed from: protected */
    public int getHelpResource() {
        return this.mHelpResource;
    }

    /* access modifiers changed from: protected */
    public String getLearnMoreContentDescription() {
        return this.mLearnMoreContentDescription;
    }

    public void setIntroductionTitle(String str) {
        this.mIntroductionTitle = str;
    }

    /* access modifiers changed from: protected */
    public String getIntroductionTitle() {
        return this.mIntroductionTitle;
    }

    private void updateFooterPreferences(AccessibilityFooterPreference accessibilityFooterPreference) {
        Intent intent;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getIntroductionTitle());
        stringBuffer.append("\n\n");
        stringBuffer.append(accessibilityFooterPreference.getTitle());
        accessibilityFooterPreference.setContentDescription(stringBuffer);
        if (getHelpResource() != 0) {
            Context context = this.mContext;
            intent = HelpUtils.getHelpIntent(context, context.getString(getHelpResource()), this.mContext.getClass().getName());
        } else {
            intent = null;
        }
        if (intent != null) {
            accessibilityFooterPreference.setLearnMoreAction(new C0597xaf7e55af(intent));
            accessibilityFooterPreference.setLearnMoreContentDescription(getLearnMoreContentDescription());
            accessibilityFooterPreference.setLinkEnabled(true);
            return;
        }
        accessibilityFooterPreference.setLinkEnabled(false);
    }
}

package com.android.settings.accounts;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.enterprise.EnterprisePrivacyFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.widget.FooterPreference;

public class EnterpriseDisclosurePreferenceController extends BasePreferenceController {
    private final EnterprisePrivacyFeatureProvider mFeatureProvider = FeatureFactory.getFactory(this.mContext).getEnterprisePrivacyFeatureProvider(this.mContext);

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

    public EnterpriseDisclosurePreferenceController(Context context, String str) {
        super(context, str);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        CharSequence disclosure = getDisclosure();
        if (disclosure != null) {
            updateFooterPreference(preferenceScreen, disclosure);
        }
    }

    public int getAvailabilityStatus() {
        return getDisclosure() == null ? 3 : 0;
    }

    /* access modifiers changed from: package-private */
    public CharSequence getDisclosure() {
        return this.mFeatureProvider.getDeviceOwnerDisclosure();
    }

    /* access modifiers changed from: package-private */
    public void updateFooterPreference(PreferenceScreen preferenceScreen, CharSequence charSequence) {
        FooterPreference footerPreference = (FooterPreference) preferenceScreen.findPreference(getPreferenceKey());
        footerPreference.setTitle(charSequence);
        footerPreference.setLearnMoreAction(new C0646x7a857cd4(this));
        footerPreference.setLearnMoreContentDescription(this.mContext.getString(C1992R$string.footer_learn_more_content_description, new Object[]{getLabelName()}));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateFooterPreference$0(View view) {
        this.mContext.startActivity(new Intent("android.settings.ENTERPRISE_PRIVACY_SETTINGS"));
    }

    private String getLabelName() {
        return this.mContext.getString(C1992R$string.header_add_an_account);
    }
}

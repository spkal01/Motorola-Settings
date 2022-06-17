package com.android.settings.notification.zen;

import android.content.Context;
import android.os.Bundle;
import com.android.settings.C1992R$string;
import com.android.settings.C1994R$xml;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class ZenCustomRuleCallsSettings extends ZenCustomRuleSettingsBase {
    public int getMetricsCategory() {
        return 1611;
    }

    /* access modifiers changed from: package-private */
    public String getPreferenceCategoryKey() {
        return "zen_mode_settings_category_calls";
    }

    public /* bridge */ /* synthetic */ int getHelpResource() {
        return super.getHelpResource();
    }

    public /* bridge */ /* synthetic */ void onAttach(Context context) {
        super.onAttach(context);
    }

    public /* bridge */ /* synthetic */ void onResume() {
        super.onResume();
    }

    public /* bridge */ /* synthetic */ void onZenModeConfigChanged() {
        super.onZenModeConfigChanged();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    /* access modifiers changed from: protected */
    public int getPreferenceScreenResId() {
        return C1994R$xml.zen_mode_custom_rule_calls_settings;
    }

    /* access modifiers changed from: protected */
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        this.mControllers = arrayList;
        arrayList.add(new ZenRuleCallsPreferenceController(context, "zen_mode_calls", getSettingsLifecycle()));
        this.mControllers.add(new ZenRuleRepeatCallersPreferenceController(context, "zen_mode_repeat_callers", getSettingsLifecycle(), context.getResources().getInteger(17694987)));
        this.mControllers.add(new ZenRuleStarredContactsPreferenceController(context, getSettingsLifecycle(), 3, "zen_mode_starred_contacts_callers"));
        return this.mControllers;
    }

    public void updatePreferences() {
        super.updatePreferences();
        getPreferenceScreen().findPreference("footer_preference").setTitle((CharSequence) this.mContext.getResources().getString(C1992R$string.zen_mode_custom_calls_footer, new Object[]{this.mRule.getName()}));
    }
}

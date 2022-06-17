package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract class ZenCustomRuleSettingsBase extends ZenModeSettingsBase {
    List<AbstractPreferenceController> mControllers = new ArrayList();
    String mId;
    AutomaticZenRule mRule;

    /* access modifiers changed from: package-private */
    public abstract String getPreferenceCategoryKey();

    ZenCustomRuleSettingsBase() {
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey("RULE_ID")) {
            Log.d("ZenCustomRuleSettings", "Rule id required to set custom dnd rule config settings");
            finish();
            return;
        }
        String string = arguments.getString("RULE_ID");
        this.mId = string;
        this.mRule = this.mBackend.getAutomaticZenRule(string);
    }

    public void onZenModeConfigChanged() {
        super.onZenModeConfigChanged();
        updatePreferences();
    }

    public void updatePreferences() {
        Preference findPreference;
        this.mRule = this.mBackend.getAutomaticZenRule(this.mId);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        String preferenceCategoryKey = getPreferenceCategoryKey();
        if (!(preferenceCategoryKey == null || (findPreference = preferenceScreen.findPreference(preferenceCategoryKey)) == null)) {
            findPreference.setTitle((CharSequence) this.mContext.getResources().getString(C1992R$string.zen_mode_custom_behavior_category_title, new Object[]{this.mRule.getName()}));
        }
        Iterator<AbstractPreferenceController> it = this.mControllers.iterator();
        while (it.hasNext()) {
            AbstractZenCustomRulePreferenceController abstractZenCustomRulePreferenceController = (AbstractZenCustomRulePreferenceController) it.next();
            abstractZenCustomRulePreferenceController.onResume(this.mRule, this.mId);
            abstractZenCustomRulePreferenceController.displayPreference(preferenceScreen);
            updatePreference(abstractZenCustomRulePreferenceController);
        }
    }

    public int getHelpResource() {
        return C1992R$string.help_uri_interruptions;
    }

    public void onResume() {
        super.onResume();
        updatePreferences();
    }

    /* access modifiers changed from: package-private */
    public Bundle createZenRuleBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("RULE_ID", this.mId);
        return bundle;
    }
}

package com.android.settings.development;

import android.content.Context;
import android.sysprop.DisplayProperties;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.development.SystemPropPoker;

public class ShowLayoutBoundsPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    public String getPreferenceKey() {
        return "debug_layout";
    }

    public ShowLayoutBoundsPreferenceController(Context context) {
        super(context);
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        DisplayProperties.debug_layout(Boolean.valueOf(((Boolean) obj).booleanValue()));
        SystemPropPoker.getInstance().poke();
        return true;
    }

    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(((Boolean) DisplayProperties.debug_layout().orElse(Boolean.FALSE)).booleanValue());
    }

    /* access modifiers changed from: protected */
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        DisplayProperties.debug_layout(Boolean.FALSE);
        ((SwitchPreference) this.mPreference).setChecked(false);
    }
}

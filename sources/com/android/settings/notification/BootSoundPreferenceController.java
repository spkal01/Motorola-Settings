package com.android.settings.notification;

import android.content.Context;
import android.os.SystemProperties;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C1980R$bool;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public class BootSoundPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    static final String PROPERTY_BOOT_SOUNDS = "persist.sys.bootanim.play_sound";

    public String getPreferenceKey() {
        return "boot_sounds";
    }

    public BootSoundPreferenceController(Context context) {
        super(context);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            ((SwitchPreference) preferenceScreen.findPreference("boot_sounds")).setChecked(SystemProperties.getBoolean(PROPERTY_BOOT_SOUNDS, true));
        }
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!"boot_sounds".equals(preference.getKey())) {
            return false;
        }
        SystemProperties.set(PROPERTY_BOOT_SOUNDS, ((SwitchPreference) preference).isChecked() ? "1" : "0");
        return false;
    }

    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(C1980R$bool.has_boot_sounds);
    }
}

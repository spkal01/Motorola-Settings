package com.motorola.settings.gestures;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import androidx.preference.Preference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.motorola.settings.utils.TriplePowerPressEmergencyCallUtil;

public class TriplePowerKeySettingObserver extends ContentObserver {
    private final Uri TRIPLE_PRESS_EMERGENCY_CALL_GESTURE = TriplePowerPressEmergencyCallUtil.getTriplePressUri();
    private final Preference mPreference;
    private AbstractPreferenceController mPreferenceController = null;

    public TriplePowerKeySettingObserver(Preference preference, AbstractPreferenceController abstractPreferenceController) {
        super(new Handler());
        this.mPreference = preference;
        this.mPreferenceController = abstractPreferenceController;
    }

    public void register(ContentResolver contentResolver) {
        contentResolver.registerContentObserver(this.TRIPLE_PRESS_EMERGENCY_CALL_GESTURE, false, this);
    }

    public void unregister(ContentResolver contentResolver) {
        contentResolver.unregisterContentObserver(this);
    }

    public void onChange(boolean z, Uri uri) {
        AbstractPreferenceController abstractPreferenceController;
        super.onChange(z, uri);
        if (this.TRIPLE_PRESS_EMERGENCY_CALL_GESTURE.equals(uri) && (abstractPreferenceController = this.mPreferenceController) != null) {
            abstractPreferenceController.updateState(this.mPreference);
        }
    }
}

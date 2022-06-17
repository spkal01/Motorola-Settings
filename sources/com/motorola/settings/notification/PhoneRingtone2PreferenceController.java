package com.motorola.settings.notification;

import android.content.Context;
import android.media.RingtoneManager;
import android.telephony.TelephonyManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settings.DefaultRingtonePreference;
import com.android.settings.Utils;
import com.android.settings.notification.RingtonePreferenceControllerBase;

public class PhoneRingtone2PreferenceController extends RingtonePreferenceControllerBase {
    private boolean mMultiSimEnabled;

    public String getPreferenceKey() {
        return "ringtone_2";
    }

    public int getRingtoneType() {
        return 64;
    }

    public PhoneRingtone2PreferenceController(Context context) {
        super(context);
        this.mMultiSimEnabled = TelephonyManager.from(context).isMultiSimEnabled();
    }

    public static boolean isDualSimRingtoneSupported() {
        return RingtoneManager.getDefaultUri(64) != null;
    }

    public static CharSequence getTitleForSlot(Context context, int i) {
        return context.getString(C1992R$string.ringtone_title_sim_slot, new Object[]{Integer.valueOf(i + 1)});
    }

    public boolean isAvailable() {
        return Utils.isVoiceCapable(this.mContext) && this.mMultiSimEnabled && isDualSimRingtoneSupported();
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        DefaultRingtonePreference defaultRingtonePreference = (DefaultRingtonePreference) preferenceScreen.findPreference(getPreferenceKey());
        if (isAvailable()) {
            defaultRingtonePreference.setRingtoneType(getRingtoneType());
        }
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setTitle(getTitleForSlot(this.mContext, 1));
    }
}

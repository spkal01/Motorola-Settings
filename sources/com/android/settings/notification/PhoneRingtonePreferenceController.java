package com.android.settings.notification;

import android.content.Context;
import android.telephony.TelephonyManager;
import androidx.preference.Preference;
import com.android.settings.Utils;
import com.motorola.settings.notification.PhoneRingtone2PreferenceController;

public class PhoneRingtonePreferenceController extends RingtonePreferenceControllerBase {
    private boolean mMultiSimEnabled;

    public String getPreferenceKey() {
        return "phone_ringtone";
    }

    public int getRingtoneType() {
        return 1;
    }

    public PhoneRingtonePreferenceController(Context context) {
        super(context);
        this.mMultiSimEnabled = TelephonyManager.from(context).isMultiSimEnabled();
    }

    public boolean isAvailable() {
        return Utils.isVoiceCapable(this.mContext);
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mMultiSimEnabled && PhoneRingtone2PreferenceController.isDualSimRingtoneSupported()) {
            preference.setTitle(PhoneRingtone2PreferenceController.getTitleForSlot(this.mContext, 0));
        }
    }
}

package com.android.settings.notification;

import android.content.Context;
import com.android.settings.C1980R$bool;

public class NotificationRingtonePreferenceController extends RingtonePreferenceControllerBase {
    public String getPreferenceKey() {
        return "notification_ringtone";
    }

    public int getRingtoneType() {
        return 2;
    }

    public NotificationRingtonePreferenceController(Context context) {
        super(context);
    }

    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(C1980R$bool.config_show_notification_ringtone);
    }
}

package com.android.settings.notification;

import android.content.Context;
import android.text.TextUtils;
import com.android.settings.C1980R$bool;
import com.android.settings.C1983R$drawable;
import com.android.settings.Utils;
import com.android.settings.slices.SliceBackgroundWorker;

public class NotificationVolumePreferenceController extends RingVolumePreferenceController {
    private static final String KEY_NOTIFICATION_VOLUME = "notification_volume";

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public int getAudioStream() {
        return 5;
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public String getPreferenceKey() {
        return KEY_NOTIFICATION_VOLUME;
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public boolean isPublicSlice() {
        return true;
    }

    public NotificationVolumePreferenceController(Context context) {
        super(context, KEY_NOTIFICATION_VOLUME);
    }

    public int getAvailabilityStatus() {
        return (!this.mContext.getResources().getBoolean(C1980R$bool.config_show_notification_volume) || Utils.isVoiceCapable(this.mContext) || this.mHelper.isSingleVolume()) ? 3 : 0;
    }

    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), KEY_NOTIFICATION_VOLUME);
    }

    public int getMuteIcon() {
        return C1983R$drawable.ic_notifications_off_24dp;
    }
}

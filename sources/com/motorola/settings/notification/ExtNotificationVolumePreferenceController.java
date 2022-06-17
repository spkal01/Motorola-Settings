package com.motorola.settings.notification;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.C1980R$bool;
import com.android.settings.C1983R$drawable;
import com.android.settings.notification.NotificationVolumePreferenceController;
import com.android.settings.notification.VolumeSeekBarPreference;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.RestrictedPreference;

public class ExtNotificationVolumePreferenceController extends NotificationVolumePreferenceController {
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public boolean useDynamicSliceSummary() {
        return true;
    }

    public ExtNotificationVolumePreferenceController(Context context) {
        super(context);
        this.mRingerMode = this.mHelper.getRingerModeInternal();
    }

    public int getAvailabilityStatus() {
        if (!this.mContext.getResources().getBoolean(C1980R$bool.config_show_notification_volume) || this.mHelper.isSingleVolume()) {
            return 3;
        }
        return isVolumeEnabled() ? 0 : 5;
    }

    public void updateState(Preference preference) {
        VolumeSeekBarPreference volumeSeekBarPreference;
        super.updateState(preference);
        if (!isDisabledByAdmin(preference) && (volumeSeekBarPreference = this.mPreference) != null) {
            volumeSeekBarPreference.setEnabled(isVolumeEnabled());
        }
    }

    /* access modifiers changed from: protected */
    public void updatePreferenceIcon() {
        VolumeSeekBarPreference volumeSeekBarPreference = this.mPreference;
        if (volumeSeekBarPreference != null) {
            int i = this.mRingerMode;
            if (i == 1) {
                int i2 = C1983R$drawable.ic_volume_ringer_vibrate;
                this.mMuteIcon = i2;
                volumeSeekBarPreference.showIcon(i2);
                this.mPreference.setEnabled(false);
            } else if (i == 0) {
                int i3 = C1983R$drawable.ic_notification_volume_silent;
                this.mMuteIcon = i3;
                volumeSeekBarPreference.showIcon(i3);
                this.mPreference.setEnabled(false);
            } else {
                volumeSeekBarPreference.showIcon(0);
                VolumeSeekBarPreference volumeSeekBarPreference2 = this.mPreference;
                volumeSeekBarPreference2.setEnabled(!volumeSeekBarPreference2.isDisabledByAdmin());
            }
        }
    }

    public int getMuteIcon() {
        return C1983R$drawable.ic_notification_volume_silent;
    }

    private boolean isVolumeEnabled() {
        int i = this.mRingerMode;
        return (i == 1 || i == 0) ? false : true;
    }

    private boolean isDisabledByAdmin(Preference preference) {
        return (preference instanceof RestrictedPreference) && ((RestrictedPreference) preference).isDisabledByAdmin();
    }
}

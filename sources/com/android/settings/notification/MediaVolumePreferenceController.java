package com.android.settings.notification;

import android.content.Context;
import android.text.TextUtils;
import com.android.settings.C1980R$bool;
import com.android.settings.C1983R$drawable;
import com.android.settings.slices.SliceBackgroundWorker;

public class MediaVolumePreferenceController extends VolumeSeekBarPreferenceController {
    private static final String KEY_MEDIA_VOLUME = "media_volume";

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public int getAudioStream() {
        return 3;
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public String getPreferenceKey() {
        return KEY_MEDIA_VOLUME;
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

    public boolean useDynamicSliceSummary() {
        return true;
    }

    public MediaVolumePreferenceController(Context context) {
        super(context, KEY_MEDIA_VOLUME);
    }

    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(C1980R$bool.config_show_media_volume) ? 0 : 3;
    }

    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), KEY_MEDIA_VOLUME);
    }

    public int getMuteIcon() {
        return C1983R$drawable.ic_media_stream_off;
    }
}

package com.android.settings.notification;

import android.content.Context;
import android.media.AudioManager;
import android.text.TextUtils;
import com.android.settings.C1980R$bool;
import com.android.settings.C1983R$drawable;
import com.android.settings.slices.SliceBackgroundWorker;

public class CallVolumePreferenceController extends VolumeSeekBarPreferenceController {
    private AudioManager mAudioManager;

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

    public boolean isPublicSlice() {
        return true;
    }

    public boolean useDynamicSliceSummary() {
        return true;
    }

    public CallVolumePreferenceController(Context context, String str) {
        super(context, str);
        this.mAudioManager = (AudioManager) context.getSystemService(AudioManager.class);
    }

    public int getAvailabilityStatus() {
        return (!this.mContext.getResources().getBoolean(C1980R$bool.config_show_call_volume) || this.mHelper.isSingleVolume()) ? 3 : 0;
    }

    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), "call_volume");
    }

    public int getAudioStream() {
        return this.mAudioManager.isBluetoothScoOn() ? 6 : 0;
    }

    public int getMuteIcon() {
        return C1983R$drawable.ic_local_phone_24_lib;
    }
}

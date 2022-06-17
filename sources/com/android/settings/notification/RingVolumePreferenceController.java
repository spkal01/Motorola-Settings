package com.android.settings.notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.settings.C1983R$drawable;
import com.android.settings.Utils;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.Objects;

public class RingVolumePreferenceController extends VolumeSeekBarPreferenceController {
    private static final String KEY_RING_VOLUME = "ring_volume";
    private static final String TAG = "RingVolumeController";
    /* access modifiers changed from: private */
    public final C1106H mHandler;
    protected int mMuteIcon;
    private final RingReceiver mReceiver;
    protected int mRingerMode;
    private ComponentName mSuppressor;
    private Vibrator mVibrator;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public int getAudioStream() {
        return 2;
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public String getPreferenceKey() {
        return KEY_RING_VOLUME;
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

    public RingVolumePreferenceController(Context context) {
        this(context, KEY_RING_VOLUME);
    }

    public RingVolumePreferenceController(Context context, String str) {
        super(context, str);
        this.mRingerMode = -1;
        this.mReceiver = new RingReceiver();
        this.mHandler = new C1106H();
        Vibrator vibrator = (Vibrator) this.mContext.getSystemService("vibrator");
        this.mVibrator = vibrator;
        if (vibrator != null && !vibrator.hasVibrator()) {
            this.mVibrator = null;
        }
        updateRingerMode();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        super.onResume();
        this.mReceiver.register(true);
        updateEffectsSuppressor();
        updatePreferenceIcon();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        super.onPause();
        this.mReceiver.register(false);
    }

    public int getAvailabilityStatus() {
        return (!Utils.isVoiceCapable(this.mContext) || this.mHelper.isSingleVolume()) ? 3 : 0;
    }

    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), KEY_RING_VOLUME);
    }

    public int getMuteIcon() {
        return this.mMuteIcon;
    }

    /* access modifiers changed from: private */
    public void updateRingerMode() {
        int ringerModeInternal = this.mHelper.getRingerModeInternal();
        if (this.mRingerMode != ringerModeInternal) {
            this.mRingerMode = ringerModeInternal;
            updatePreferenceIcon();
        }
    }

    /* access modifiers changed from: private */
    public void updateEffectsSuppressor() {
        ComponentName effectsSuppressor = NotificationManager.from(this.mContext).getEffectsSuppressor();
        if (!Objects.equals(effectsSuppressor, this.mSuppressor)) {
            this.mSuppressor = effectsSuppressor;
            if (this.mPreference != null) {
                this.mPreference.setSuppressionText(SuppressorHelper.getSuppressionText(this.mContext, effectsSuppressor));
            }
            updatePreferenceIcon();
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
            } else if (i == 0) {
                int i3 = C1983R$drawable.ic_notifications_off_24dp;
                this.mMuteIcon = i3;
                volumeSeekBarPreference.showIcon(i3);
            } else {
                volumeSeekBarPreference.showIcon(C1983R$drawable.ic_notifications);
            }
        }
    }

    /* renamed from: com.android.settings.notification.RingVolumePreferenceController$H */
    private final class C1106H extends Handler {
        private C1106H() {
            super(Looper.getMainLooper());
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                RingVolumePreferenceController.this.updateEffectsSuppressor();
            } else if (i == 2) {
                RingVolumePreferenceController.this.updateRingerMode();
            }
        }
    }

    private class RingReceiver extends BroadcastReceiver {
        private boolean mRegistered;

        private RingReceiver() {
        }

        public void register(boolean z) {
            if (this.mRegistered != z) {
                if (z) {
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED");
                    intentFilter.addAction("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION");
                    RingVolumePreferenceController.this.mContext.registerReceiver(this, intentFilter);
                } else {
                    RingVolumePreferenceController.this.mContext.unregisterReceiver(this);
                }
                this.mRegistered = z;
            }
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.os.action.ACTION_EFFECTS_SUPPRESSOR_CHANGED".equals(action)) {
                RingVolumePreferenceController.this.mHandler.sendEmptyMessage(1);
            } else if ("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION".equals(action)) {
                RingVolumePreferenceController.this.mHandler.sendEmptyMessage(2);
            }
        }
    }
}

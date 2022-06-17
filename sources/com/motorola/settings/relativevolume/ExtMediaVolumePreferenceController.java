package com.motorola.settings.relativevolume;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.notification.MediaVolumePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settings.relativevolume.RelativeVolumePreference;

public class ExtMediaVolumePreferenceController extends MediaVolumePreferenceController {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = RelativeVolumeUtils.DEBUG;
    private static final String TAG = "RV-ExtMediaVolumePrefCtrl";
    final RelativeVolumeCallback mRelativeVolumeCallback = new RelativeVolumeCallback();
    private RelativeVolumePreference mRelativeVolumePreference;

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

    public ExtMediaVolumePreferenceController(Context context) {
        super(context);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        RelativeVolumePreference relativeVolumePreference;
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            this.mRelativeVolumePreference = (RelativeVolumePreference) preferenceScreen.findPreference(RelativeVolumePreferenceController.KEY_RELATIVE_VOLUME);
            if (RelativeVolumeUtils.isRelativeVolumeFeatureOn(this.mContext) && (relativeVolumePreference = this.mRelativeVolumePreference) != null) {
                relativeVolumePreference.setAppVolumeCallBack(this.mRelativeVolumeCallback);
            }
        }
    }

    final class RelativeVolumeCallback implements RelativeVolumePreference.AppVolumeCallback {
        RelativeVolumeCallback() {
        }

        public void onAppValueChanged(int i, boolean z) {
            if (ExtMediaVolumePreferenceController.DEBUG) {
                Log.d(ExtMediaVolumePreferenceController.TAG, "onAppValueChanged: " + i + " updateMaster: " + z);
            }
            if (z && ExtMediaVolumePreferenceController.this.mPreference != null && ExtMediaVolumePreferenceController.this.mPreference.isMusicStream()) {
                ExtMediaVolumePreferenceController.this.mPreference.updateWhenAppVolumeChanged(i);
            }
        }

        public void onUserAttemptChange(long j) {
            if (ExtMediaVolumePreferenceController.DEBUG) {
                Log.d(ExtMediaVolumePreferenceController.TAG, "onUserAttemptChange: " + j);
            }
            if (ExtMediaVolumePreferenceController.this.mPreference != null && ExtMediaVolumePreferenceController.this.mPreference.isMusicStream()) {
                ExtMediaVolumePreferenceController.this.mPreference.updateUserAttemptTime(j);
            }
        }

        public void onSafeVolumeAction(int i) {
            if (ExtMediaVolumePreferenceController.DEBUG) {
                Log.d(ExtMediaVolumePreferenceController.TAG, "onSafeVolumeAction: " + i);
            }
            if ((i == 1 || i == 3) && RelativeVolumeUtils.isRelativeVolumeFeatureOn(ExtMediaVolumePreferenceController.this.mContext)) {
                int streamVolume = ((AudioManager) ExtMediaVolumePreferenceController.this.mContext.getSystemService("audio")).getStreamVolume(3);
                if (ExtMediaVolumePreferenceController.DEBUG) {
                    Log.d(ExtMediaVolumePreferenceController.TAG, "onSafeVolumeAction show or dismiss safe-volume dialog, masterVolume: " + streamVolume);
                }
                if (ExtMediaVolumePreferenceController.this.mPreference != null && ExtMediaVolumePreferenceController.this.mPreference.isMusicStream()) {
                    ExtMediaVolumePreferenceController.this.mPreference.setSafeVolume(streamVolume);
                }
            }
        }
    }
}

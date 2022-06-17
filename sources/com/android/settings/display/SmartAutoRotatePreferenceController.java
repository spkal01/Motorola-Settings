package com.android.settings.display;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorPrivacyManager;
import android.os.PowerManager;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.view.RotationPolicy;
import com.android.settings.C1992R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class SmartAutoRotatePreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private final PowerManager mPowerManager;
    /* access modifiers changed from: private */
    public Preference mPreference;
    private final SensorPrivacyManager mPrivacyManager;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            SmartAutoRotatePreferenceController smartAutoRotatePreferenceController = SmartAutoRotatePreferenceController.this;
            smartAutoRotatePreferenceController.refreshSummary(smartAutoRotatePreferenceController.mPreference);
        }
    };
    private RotationPolicy.RotationPolicyListener mRotationPolicyListener;

    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public SmartAutoRotatePreferenceController(Context context, String str) {
        super(context, str);
        SensorPrivacyManager instance = SensorPrivacyManager.getInstance(context);
        this.mPrivacyManager = instance;
        instance.addSensorPrivacyListener(2, new SmartAutoRotatePreferenceController$$ExternalSyntheticLambda0(this));
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, boolean z) {
        refreshSummary(this.mPreference);
    }

    public int getAvailabilityStatus() {
        return RotationPolicy.isRotationLockToggleVisible(this.mContext) ? 0 : 3;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    public void onStart() {
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter("android.os.action.POWER_SAVE_MODE_CHANGED"));
        if (this.mRotationPolicyListener == null) {
            this.mRotationPolicyListener = new RotationPolicy.RotationPolicyListener() {
                public void onChange() {
                    if (SmartAutoRotatePreferenceController.this.mPreference != null) {
                        SmartAutoRotatePreferenceController smartAutoRotatePreferenceController = SmartAutoRotatePreferenceController.this;
                        smartAutoRotatePreferenceController.refreshSummary(smartAutoRotatePreferenceController.mPreference);
                    }
                }
            };
        }
        RotationPolicy.registerRotationPolicyListener(this.mContext, this.mRotationPolicyListener);
    }

    public void onStop() {
        this.mContext.unregisterReceiver(this.mReceiver);
        RotationPolicy.RotationPolicyListener rotationPolicyListener = this.mRotationPolicyListener;
        if (rotationPolicyListener != null) {
            RotationPolicy.unregisterRotationPolicyListener(this.mContext, rotationPolicyListener);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isCameraLocked() {
        return this.mPrivacyManager.isSensorPrivacyEnabled(2);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isPowerSaveMode() {
        return this.mPowerManager.isPowerSaveMode();
    }

    public CharSequence getSummary() {
        int i = C1992R$string.auto_rotate_option_off;
        if (!RotationPolicy.isRotationLocked(this.mContext)) {
            if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "camera_autorotate", 0, -2) != 1 || !SmartAutoRotateController.isRotationResolverServiceAvailable(this.mContext) || !SmartAutoRotateController.hasSufficientPermission(this.mContext) || isCameraLocked() || isPowerSaveMode()) {
                i = C1992R$string.auto_rotate_option_on;
            } else {
                i = C1992R$string.auto_rotate_option_face_based;
            }
        }
        return this.mContext.getString(i);
    }
}

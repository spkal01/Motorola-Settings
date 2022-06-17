package com.motorola.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.ims.ImsManager;
import com.android.settings.network.ims.VoNRQueryImsState;
import com.android.settings.network.telephony.TelephonyTogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.android.telephony.MotoExtTelephonyManager;
import com.motorola.settings.network.cache.MotoMnsCache;

public class VoNRPreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver {
    private static final String LOG_TAG = "VoNRPreferenceController";
    /* access modifiers changed from: private */
    public Integer mCallState;
    /* access modifiers changed from: private */
    public SwitchPreference mPreference;
    /* access modifiers changed from: private */
    public PhoneCallStateTelephonyCallback mTelephonyCallback;
    private MotoExtTelephonyManager motTm;

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

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public VoNRPreferenceController(Context context, String str) {
        super(context, str);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        super.displayPreference(preferenceScreen);
    }

    public void init(Lifecycle lifecycle, int i) {
        this.mTelephonyCallback = new PhoneCallStateTelephonyCallback();
        this.mSubId = i;
        this.motTm = new MotoExtTelephonyManager(this.mContext, this.mSubId);
        updateCallState();
        lifecycle.addObserver(this);
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference != null) {
            ((SwitchPreference) preference).setEnabled(isCallStateIdle() && queryImsState(this.mSubId).isAllowUserControl());
        }
    }

    public boolean isChecked() {
        try {
            boolean voNRSetting = this.motTm.getVoNRSetting();
            MotoMnsLog.logd(LOG_TAG, "isChecked isVoNREnabled: " + voNRSetting);
            return voNRSetting;
        } catch (Exception e) {
            MotoMnsLog.logd(LOG_TAG, "isChecked getVoNRSetting failed: " + e);
            return false;
        }
    }

    public boolean setChecked(boolean z) {
        try {
            boolean voNRSetting = this.motTm.setVoNRSetting(z);
            MotoMnsLog.logd(LOG_TAG, "setChecked isVoNRSet: " + voNRSetting);
            return voNRSetting;
        } catch (Exception e) {
            MotoMnsLog.logd(LOG_TAG, "setChecked setVoNRSetting failed: " + e);
            return false;
        }
    }

    public int getAvailabilityStatus(int i) {
        updateCallState();
        if (!MotoTelephonyFeature.isVoNRMenuNeeded(this.mContext, i)) {
            return 2;
        }
        ImsManager instance = ImsManager.getInstance(this.mContext.getApplicationContext(), MotoMnsCache.getIns(this.mContext).getPhoneId(i));
        if (instance == null || !instance.isImsOverNrEnabledByPlatform()) {
            MotoMnsLog.logd(LOG_TAG, "isImsOverNrEnabledByPlatform: false");
            return 2;
        }
        VoNRQueryImsState queryImsState = queryImsState(i);
        if (!queryImsState.isReadyToVoNR()) {
            MotoMnsLog.logd(LOG_TAG, "isReadyToVoNR: false");
            return 2;
        }
        boolean isCallStateIdle = isCallStateIdle();
        boolean isAllowUserControl = queryImsState.isAllowUserControl();
        MotoMnsLog.logd(LOG_TAG, "isCallStateIdle:" + isCallStateIdle + ", isAllowUserControl:" + isAllowUserControl);
        return (!isCallStateIdle || !isAllowUserControl) ? 1 : 0;
    }

    private void updateCallState() {
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            telephonyManager = telephonyManager.createForSubscriptionId(this.mSubId);
        }
        this.mCallState = Integer.valueOf(telephonyManager.getCallStateForSubscription());
    }

    /* access modifiers changed from: protected */
    public boolean isCallStateIdle() {
        Integer num = this.mCallState;
        return num != null && num.intValue() == 0;
    }

    /* access modifiers changed from: protected */
    public VoNRQueryImsState queryImsState(int i) {
        return new VoNRQueryImsState(this.mContext, i);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        PhoneCallStateTelephonyCallback phoneCallStateTelephonyCallback = this.mTelephonyCallback;
        if (phoneCallStateTelephonyCallback != null) {
            phoneCallStateTelephonyCallback.register(this.mContext, this.mSubId);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        PhoneCallStateTelephonyCallback phoneCallStateTelephonyCallback = this.mTelephonyCallback;
        if (phoneCallStateTelephonyCallback != null) {
            phoneCallStateTelephonyCallback.unregister();
        }
    }

    private class PhoneCallStateTelephonyCallback extends TelephonyCallback implements TelephonyCallback.CallStateListener {
        private TelephonyManager mTelephonyManager;

        private PhoneCallStateTelephonyCallback() {
        }

        public void onCallStateChanged(int i) {
            Integer unused = VoNRPreferenceController.this.mCallState = Integer.valueOf(i);
            VoNRPreferenceController voNRPreferenceController = VoNRPreferenceController.this;
            voNRPreferenceController.updateState(voNRPreferenceController.mPreference);
        }

        public void register(Context context, int i) {
            this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
            if (SubscriptionManager.isValidSubscriptionId(i)) {
                this.mTelephonyManager = this.mTelephonyManager.createForSubscriptionId(i);
            }
            Integer unused = VoNRPreferenceController.this.mCallState = Integer.valueOf(this.mTelephonyManager.getCallState(i));
            this.mTelephonyManager.registerTelephonyCallback(VoNRPreferenceController.this.mContext.getMainExecutor(), VoNRPreferenceController.this.mTelephonyCallback);
        }

        public void unregister() {
            Integer unused = VoNRPreferenceController.this.mCallState = null;
            TelephonyManager telephonyManager = this.mTelephonyManager;
            if (telephonyManager != null) {
                telephonyManager.unregisterTelephonyCallback(this);
            }
        }
    }
}

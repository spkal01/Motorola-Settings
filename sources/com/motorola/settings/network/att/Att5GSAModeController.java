package com.motorola.settings.network.att;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C1992R$string;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.utils.ThreadUtils;
import com.motorola.android.telephony.MotoExtTelephonyManager;
import com.motorola.settings.network.MotoMnsLog;
import com.motorola.settings.network.MotoMobileNetworkUtils;
import com.motorola.settings.network.MotoTelephonyFeature;
import com.motorola.settings.network.MotoTelephonyTogglePreferenceController;

public class Att5GSAModeController extends MotoTelephonyTogglePreferenceController implements LifecycleOwner {
    /* access modifiers changed from: private */
    public static final String TAG = "Att5GSAModeController";
    private final AlertDialog mConfirmationDialog;
    private Lifecycle mLifecycle;
    private final LiveData<Boolean> mPhoneIdleLiveData;
    private final PhoneUtil mPhoneUtil;
    private SwitchPreference mPreference;

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

    public Att5GSAModeController(Context context, String str) {
        super(context, str);
        PhoneUtil phoneUtil = new PhoneUtil(context, this.mSubId);
        this.mPhoneUtil = phoneUtil;
        this.mPhoneIdleLiveData = new PhoneIdleLiveData(phoneUtil);
        this.mConfirmationDialog = new AlertDialog.Builder(context).setMessage(C1992R$string.carrier_dialog5g_summary).setTitle(C1992R$string.carrier_dialog5g_title).setCancelable(true).setPositiveButton(C1992R$string.carrier_dialog_button_yes, new Att5GSAModeController$$ExternalSyntheticLambda0(this)).setNegativeButton(17039369, (DialogInterface.OnClickListener) null).setOnDismissListener(new Att5GSAModeController$$ExternalSyntheticLambda1(this)).create();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(DialogInterface dialogInterface, int i) {
        this.mPhoneUtil.set5gSaEnabled(1);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(DialogInterface dialogInterface) {
        updateState(this.mPreference);
    }

    /* access modifiers changed from: package-private */
    public Att5GSAModeController init(Lifecycle lifecycle) {
        this.mLifecycle = lifecycle;
        this.mPhoneIdleLiveData.observe(this, new Att5GSAModeController$$ExternalSyntheticLambda2(this));
        return this;
    }

    public Lifecycle getLifecycle() {
        return this.mLifecycle;
    }

    public int getAvailabilityStatus(int i) {
        return (!MotoTelephonyFeature.isCarrier5GOptionEnabled(this.mContext, i) || !MotoMobileNetworkUtils.carrierAllowed5g(this.mContext, i)) ? 2 : 1;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    public boolean isChecked() {
        int is5gSaEnabled = this.mPhoneUtil.is5gSaEnabled();
        String str = TAG;
        MotoMnsLog.logd(str, "isChecked: is5gEnabled=" + is5gSaEnabled);
        if (is5gSaEnabled == -1) {
            is5gSaEnabled = MotoMobileNetworkUtils.getCarrier5GOptionDefaultValue(this.mContext, this.mSubId);
        }
        return is5gSaEnabled == 0 || is5gSaEnabled == 2;
    }

    public boolean setChecked(boolean z) {
        String str = TAG;
        MotoMnsLog.logd(str, "setChecked: isChecked=" + z);
        if (z) {
            this.mPhoneUtil.set5gSaEnabled(0);
            return true;
        }
        this.mConfirmationDialog.show();
        return false;
    }

    /* access modifiers changed from: private */
    public void onCallIdleStateChanged(Boolean bool) {
        String str = TAG;
        MotoMnsLog.logd(str, "onCallIdleStateChanged: isCallStateIdle=" + bool);
        this.mPreference.setEnabled(bool.booleanValue());
        this.mConfirmationDialog.dismiss();
    }

    private static class PhoneIdleLiveData extends MutableLiveData<Boolean> {
        private final PhoneStateListener listener = new PhoneStateListener() {
            public void onCallStateChanged(int i, String str) {
                String access$000 = Att5GSAModeController.TAG;
                MotoMnsLog.logd(access$000, "PhoneIdleLiveData: onCallStateChanged state=" + i);
                PhoneIdleLiveData.this.updateValue(i);
            }
        };
        private final PhoneUtil mPhoneUtil;

        PhoneIdleLiveData(PhoneUtil phoneUtil) {
            this.mPhoneUtil = phoneUtil;
        }

        /* access modifiers changed from: protected */
        public void onActive() {
            super.onActive();
            MotoMnsLog.logd(Att5GSAModeController.TAG, "PhoneIdleLiveData: onActive");
            updateValue(this.mPhoneUtil.getCallState());
            this.mPhoneUtil.startListeningCallState(this.listener);
        }

        /* access modifiers changed from: protected */
        public void onInactive() {
            super.onInactive();
            MotoMnsLog.logd(Att5GSAModeController.TAG, "PhoneIdleLiveData: onInactive");
            this.mPhoneUtil.stopListeningCallState(this.listener);
        }

        /* access modifiers changed from: private */
        public void updateValue(int i) {
            boolean z = i == 0;
            String access$000 = Att5GSAModeController.TAG;
            MotoMnsLog.logd(access$000, "PhoneIdleLiveData: updateValue isCallStateIdle=" + z);
            setValue(Boolean.valueOf(z));
        }
    }

    private static class PhoneUtil {
        private final TelephonyManager mTelephonyManager;
        private MotoExtTelephonyManager motTm;

        PhoneUtil(Context context, int i) {
            this.mTelephonyManager = TelephonyManager.from(context).createForSubscriptionId(i);
            this.motTm = new MotoExtTelephonyManager(context, i);
        }

        /* access modifiers changed from: package-private */
        public void set5gSaEnabled(int i) {
            ThreadUtils.postOnBackgroundThread((Runnable) new Att5GSAModeController$PhoneUtil$$ExternalSyntheticLambda0(this, i));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$set5gSaEnabled$0(int i) {
            try {
                this.motTm.setNrModeDisabled(i);
            } catch (Exception unused) {
                String access$000 = Att5GSAModeController.TAG;
                MotoMnsLog.logd(access$000, "setNrMode failed mode:" + i);
            }
            MotoMnsLog.logd(Att5GSAModeController.TAG, "PhoneUtil: enable5gData succeeded");
        }

        /* access modifiers changed from: package-private */
        public int is5gSaEnabled() {
            try {
                return this.motTm.getNrModeDisabled();
            } catch (Exception e) {
                String access$000 = Att5GSAModeController.TAG;
                MotoMnsLog.logd(access$000, "getNrMode failed:" + e);
                return -1;
            }
        }

        /* access modifiers changed from: package-private */
        public int getCallState() {
            return this.mTelephonyManager.getCallState();
        }

        /* access modifiers changed from: package-private */
        public void startListeningCallState(PhoneStateListener phoneStateListener) {
            this.mTelephonyManager.listen(phoneStateListener, 32);
        }

        /* access modifiers changed from: package-private */
        public void stopListeningCallState(PhoneStateListener phoneStateListener) {
            this.mTelephonyManager.listen(phoneStateListener, 0);
        }
    }
}

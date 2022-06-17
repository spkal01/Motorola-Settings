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
import com.motorola.settings.network.MotoMnsLog;
import com.motorola.settings.network.MotoTelephonyFeature;
import com.motorola.settings.network.MotoTelephonyTogglePreferenceController;

public class AttDisable2GController extends MotoTelephonyTogglePreferenceController implements LifecycleOwner {
    /* access modifiers changed from: private */
    public static final String TAG = "AttDisable2GController";
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

    public AttDisable2GController(Context context, String str) {
        super(context, str);
        PhoneUtil phoneUtil = new PhoneUtil(context, this.mSubId);
        this.mPhoneUtil = phoneUtil;
        this.mPhoneIdleLiveData = new PhoneIdleLiveData(phoneUtil);
        this.mConfirmationDialog = new AlertDialog.Builder(context).setMessage(C1992R$string.att_disable2g_confirmation).setTitle(C1992R$string.att_disable2g_title).setCancelable(true).setPositiveButton(17039379, new AttDisable2GController$$ExternalSyntheticLambda0(this)).setNegativeButton(17039369, (DialogInterface.OnClickListener) null).setOnDismissListener(new AttDisable2GController$$ExternalSyntheticLambda1(this)).create();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(DialogInterface dialogInterface, int i) {
        this.mPhoneUtil.disable2gData();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(DialogInterface dialogInterface) {
        updateState(this.mPreference);
    }

    /* access modifiers changed from: package-private */
    public AttDisable2GController init(Lifecycle lifecycle) {
        this.mLifecycle = lifecycle;
        this.mPhoneIdleLiveData.observe(this, new AttDisable2GController$$ExternalSyntheticLambda2(this));
        return this;
    }

    public Lifecycle getLifecycle() {
        return this.mLifecycle;
    }

    public int getAvailabilityStatus(int i) {
        return MotoTelephonyFeature.isFtr5083Enabled(this.mContext, i) ? 1 : 2;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    public boolean isChecked() {
        boolean is2gEnabled = this.mPhoneUtil.is2gEnabled();
        String str = TAG;
        MotoMnsLog.logd(str, "isChecked: is2gEnabled=" + is2gEnabled);
        return is2gEnabled;
    }

    public boolean setChecked(boolean z) {
        String str = TAG;
        MotoMnsLog.logd(str, "setChecked: isChecked=" + z);
        if (z) {
            this.mPhoneUtil.enable2gData();
            return true;
        }
        this.mConfirmationDialog.show();
        MotoMnsLog.logd(str, "setChecked: Show dialog to disable 2g");
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
                String access$000 = AttDisable2GController.TAG;
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
            MotoMnsLog.logd(AttDisable2GController.TAG, "PhoneIdleLiveData: onActive");
            updateValue(this.mPhoneUtil.getCallState());
            this.mPhoneUtil.startListeningCallState(this.listener);
        }

        /* access modifiers changed from: protected */
        public void onInactive() {
            super.onInactive();
            MotoMnsLog.logd(AttDisable2GController.TAG, "PhoneIdleLiveData: onInactive");
            this.mPhoneUtil.stopListeningCallState(this.listener);
        }

        /* access modifiers changed from: private */
        public void updateValue(int i) {
            boolean z = i == 0;
            String access$000 = AttDisable2GController.TAG;
            MotoMnsLog.logd(access$000, "PhoneIdleLiveData: updateValue isCallStateIdle=" + z);
            setValue(Boolean.valueOf(z));
        }
    }

    private static class PhoneUtil {
        private final TelephonyManager mTelephonyManager;

        PhoneUtil(Context context, int i) {
            this.mTelephonyManager = TelephonyManager.from(context).createForSubscriptionId(i);
        }

        /* access modifiers changed from: package-private */
        public void disable2gData() {
            ThreadUtils.postOnBackgroundThread((Runnable) new AttDisable2GController$PhoneUtil$$ExternalSyntheticLambda1(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$disable2gData$0() {
            this.mTelephonyManager.setAllowedNetworkTypesForReason(3, getGSMBitMask(false));
            MotoMnsLog.logd(AttDisable2GController.TAG, "PhoneUtil: disable2gData succeeded");
        }

        /* access modifiers changed from: package-private */
        public void enable2gData() {
            ThreadUtils.postOnBackgroundThread((Runnable) new AttDisable2GController$PhoneUtil$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$enable2gData$1() {
            this.mTelephonyManager.setAllowedNetworkTypesForReason(3, getGSMBitMask(true));
            MotoMnsLog.logd(AttDisable2GController.TAG, "PhoneUtil: enable2gData succeeded");
        }

        /* access modifiers changed from: package-private */
        public boolean is2gEnabled() {
            return (this.mTelephonyManager.getAllowedNetworkTypesForReason(3) & 32771) != 0;
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

        /* access modifiers changed from: package-private */
        public long getGSMBitMask(boolean z) {
            long allowedNetworkTypesForReason = this.mTelephonyManager.getAllowedNetworkTypesForReason(3);
            if (z) {
                MotoMnsLog.logd(AttDisable2GController.TAG, "PhoneUtil: enable2gData ");
                return 32771 | allowedNetworkTypesForReason;
            }
            MotoMnsLog.logd(AttDisable2GController.TAG, "PhoneUtil: Disabl2gData ");
            return -32772 & allowedNetworkTypesForReason;
        }
    }
}

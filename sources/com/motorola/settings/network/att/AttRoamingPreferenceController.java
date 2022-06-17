package com.motorola.settings.network.att;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settings.network.MobileDataContentObserver;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.RestrictedSwitchPreference;
import com.motorola.settings.network.ExtendedRoamingPreferenceController;
import com.motorola.settings.network.MotoMnsLog;
import com.motorola.settings.network.MotoMobileNetworkUtils;

public class AttRoamingPreferenceController extends ExtendedRoamingPreferenceController {
    public static final String TAG = "AttRoamingPref";
    private MobileDataContentObserver mMobileDataContentObserver;
    private Intent mMotRoamingDialogIntent;
    private RestrictedSwitchPreference mPreference;
    private TelephonyManager mTelephonyManager;

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

    public AttRoamingPreferenceController(Context context, String str) {
        super(context, str);
        MobileDataContentObserver mobileDataContentObserver = new MobileDataContentObserver(new Handler(Looper.getMainLooper()));
        this.mMobileDataContentObserver = mobileDataContentObserver;
        mobileDataContentObserver.setOnMobileDataChangedListener(new AttRoamingPreferenceController$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        updateState(this.mPreference);
    }

    public int getAvailabilityStatus(int i) {
        if (this.mTelephonyManager == null) {
            this.mTelephonyManager = TelephonyManager.from(this.mContext).createForSubscriptionId(this.mSubId);
        }
        if (!this.mTelephonyManager.isDataEnabled()) {
            return 5;
        }
        return super.getAvailabilityStatus(i);
    }

    public void onStart() {
        super.onStart();
        this.mMobileDataContentObserver.register(this.mContext, this.mSubId);
    }

    public void onStop() {
        super.onStop();
        this.mMobileDataContentObserver.unRegister(this.mContext);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
        if (!restrictedSwitchPreference.isDisabledByAdmin()) {
            restrictedSwitchPreference.setEnabled(this.mTelephonyManager.isDataEnabled());
            restrictedSwitchPreference.setChecked(isChecked());
        }
    }

    public boolean setChecked(boolean z) {
        this.mMotRoamingDialogIntent = getMotoRoamingDialogIntent(z);
        MotoMnsLog.logd(TAG, "mMotRoamingDialogIntent:" + this.mMotRoamingDialogIntent);
        if (this.mMotRoamingDialogIntent == null) {
            return super.setChecked(z);
        }
        return false;
    }

    private Intent getMotoRoamingDialogIntent(boolean z) {
        int i;
        if (z) {
            i = C1992R$string.roaming_data_enable_dialog_intent;
        } else {
            i = C1992R$string.roaming_data_disable_dialog_intent;
        }
        Context context = this.mContext;
        return MotoMobileNetworkUtils.getIntentResolvedToSystemActivity(context, context.getString(i));
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        Intent intent;
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey()) || (intent = this.mMotRoamingDialogIntent) == null) {
            return super.handlePreferenceTreeClick(preference);
        }
        this.mContext.startActivity(intent);
        return true;
    }
}

package com.android.settings.network.telephony.gsm;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.ServiceState;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.network.AllowedNetworkTypesListener;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.network.telephony.NetworkSelectSettings;
import com.android.settings.network.telephony.TelephonyBasePreferenceController;
import com.android.settings.network.telephony.gsm.AutoSelectPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.concurrent.Executor;

public class OpenNetworkSelectPagePreferenceController extends TelephonyBasePreferenceController implements AutoSelectPreferenceController.OnNetworkSelectModeListener, LifecycleObserver {
    private AllowedNetworkTypesListener mAllowedNetworkTypesListener;
    private PhoneServiceStateListener mPhoneStateListener;
    /* access modifiers changed from: private */
    public Preference mPreference;
    private PreferenceScreen mPreferenceScreen;
    /* access modifiers changed from: private */
    public TelephonyManager mTelephonyManager;

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

    public OpenNetworkSelectPagePreferenceController(Context context, String str) {
        super(context, str);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mSubId = -1;
        AllowedNetworkTypesListener allowedNetworkTypesListener = new AllowedNetworkTypesListener(context.getMainExecutor());
        this.mAllowedNetworkTypesListener = allowedNetworkTypesListener;
        allowedNetworkTypesListener.setAllowedNetworkTypesListener(new C1090xcfe1fbf5(this));
        this.mPhoneStateListener = new PhoneServiceStateListener(context.getMainExecutor());
    }

    /* access modifiers changed from: private */
    /* renamed from: updatePreference */
    public void lambda$new$0() {
        PreferenceScreen preferenceScreen = this.mPreferenceScreen;
        if (preferenceScreen != null) {
            displayPreference(preferenceScreen);
        }
        Preference preference = this.mPreference;
        if (preference != null) {
            updateState(preference);
        }
    }

    public int getAvailabilityStatus(int i) {
        return MobileNetworkUtils.shouldDisplayNetworkSelectOptions(this.mContext, i) ? 0 : 2;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mAllowedNetworkTypesListener.register(this.mContext, this.mSubId);
        this.mPhoneStateListener.register();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mAllowedNetworkTypesListener.unregister(this.mContext, this.mSubId);
        this.mPhoneStateListener.unregister();
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        boolean z = true;
        if (this.mTelephonyManager.getNetworkSelectionMode() == 1) {
            z = false;
        }
        preference.setEnabled(z);
    }

    public CharSequence getSummary() {
        ServiceState serviceState = this.mTelephonyManager.getServiceState();
        if (serviceState == null || serviceState.getState() != 0) {
            return this.mContext.getString(C1992R$string.network_disconnected);
        }
        return MobileNetworkUtils.getCurrentCarrierNameForDisplay(this.mContext, this.mSubId);
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("android.provider.extra.SUB_ID", this.mSubId);
        new SubSettingLauncher(this.mContext).setDestination(NetworkSelectSettings.class.getName()).setSourceMetricsCategory(1581).setTitleRes(C1992R$string.choose_network_title).setArguments(bundle).launch();
        return true;
    }

    public OpenNetworkSelectPagePreferenceController init(Lifecycle lifecycle, int i) {
        this.mSubId = i;
        this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        lifecycle.addObserver(this);
        return this;
    }

    public void onNetworkSelectModeChanged() {
        updateState(this.mPreference);
    }

    private class PhoneServiceStateListener extends TelephonyCallback implements TelephonyCallback.ServiceStateListener {
        private Executor mExecutor;

        public PhoneServiceStateListener(Executor executor) {
            this.mExecutor = executor;
        }

        public void onServiceStateChanged(ServiceState serviceState) {
            OpenNetworkSelectPagePreferenceController openNetworkSelectPagePreferenceController = OpenNetworkSelectPagePreferenceController.this;
            openNetworkSelectPagePreferenceController.updateState(openNetworkSelectPagePreferenceController.mPreference);
        }

        public void register() {
            OpenNetworkSelectPagePreferenceController.this.mTelephonyManager.registerTelephonyCallback(this.mExecutor, this);
        }

        public void unregister() {
            OpenNetworkSelectPagePreferenceController.this.mTelephonyManager.unregisterTelephonyCallback(this);
        }
    }
}

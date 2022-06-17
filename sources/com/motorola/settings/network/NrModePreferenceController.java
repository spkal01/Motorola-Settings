package com.motorola.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import android.telephony.CarrierConfigManager;
import android.telephony.TelephonyManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.network.telephony.TelephonyTogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.android.telephony.MotoExtTelephonyManager;

public class NrModePreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver {
    private static final String LOG_TAG = "NrModePreferenceController";
    private CarrierConfigManager mConfigManager;
    private SwitchPreference mPreference;
    private TelephonyManager mTelephonyManager;
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

    public NrModePreferenceController(Context context, String str) {
        super(context, str);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = switchPreference;
        if (switchPreference != null) {
            super.displayPreference(preferenceScreen);
        }
    }

    public void init(Lifecycle lifecycle, int i) {
        this.mSubId = i;
        this.mTelephonyManager = TelephonyManager.from(this.mContext).createForSubscriptionId(this.mSubId);
        this.mConfigManager = (CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class);
        this.motTm = new MotoExtTelephonyManager(this.mContext, this.mSubId);
        lifecycle.addObserver(this);
    }

    public boolean isChecked() {
        int nrModeDisabled = getNrModeDisabled();
        MotoMnsLog.logd(LOG_TAG, "isChecked mode:" + nrModeDisabled);
        return nrModeDisabled == 0 || nrModeDisabled == 2;
    }

    public boolean setChecked(boolean z) {
        MotoMnsLog.logd(LOG_TAG, "setChecked:" + z);
        setNrMode(z ^ true ? 1 : 0);
        return true;
    }

    public int getAvailabilityStatus(int i) {
        return (!MotoTelephonyFeature.is5gModeNeeded(this.mContext, i) || !MotoMobileNetworkUtils.modemSupports5g(this.mContext, i) || !MotoMobileNetworkUtils.carrierSupports5gByCarrierConfig(this.mContext, i) || (!MotoMobileNetworkUtils.isDds(i) && (!MotoMobileNetworkUtils.secondarySimSupportsUpTo5g() || !MotoMobileNetworkUtils.isSecondarySimSupportSA(this.mContext)))) ? 2 : 0;
    }

    private void setNrMode(int i) {
        try {
            this.motTm.setNrModeDisabled(i);
        } catch (Exception unused) {
            MotoMnsLog.logd(LOG_TAG, "setNrMode failed mode:" + i);
        }
    }

    private int getNrModeDisabled() {
        try {
            return this.motTm.getNrModeDisabled();
        } catch (Exception e) {
            MotoMnsLog.logd(LOG_TAG, "getNrMode failed:" + e);
            return -1;
        }
    }

    public void refreshPreference() {
        boolean isAvailable = isAvailable();
        SwitchPreference switchPreference = this.mPreference;
        if (switchPreference != null) {
            switchPreference.setVisible(isAvailable);
            if (isAvailable) {
                this.mPreference.setOnPreferenceChangeListener(this);
                this.mPreference.setChecked(isChecked());
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        refreshPreference();
    }
}

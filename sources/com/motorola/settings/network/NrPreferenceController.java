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
import com.android.settings.network.AllowedNetworkTypesListener;
import com.android.settings.network.telephony.TelephonyTogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.motorola.settingslib.moto5gmenu.Moto5gMenuUtils;

public class NrPreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver {
    private static final String LOG_TAG = "NrPreferenceController";
    private AllowedNetworkTypesListener mAllowedNetworkTypesListener;
    private CarrierConfigManager mConfigManager;
    private boolean mIs5gNetworkTypeAllowed;
    private boolean mIs5gSwitchToggled;
    private SwitchPreference mPreference;
    private TelephonyManager mTelephonyManager;

    private boolean checkSupportedRadioBitmask(long j, long j2) {
        return (j2 & j) > 0;
    }

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

    public NrPreferenceController(Context context, String str) {
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
        this.mIs5gNetworkTypeAllowed = is5gNetworkTypeAllowed();
        this.mIs5gSwitchToggled = false;
        if (this.mAllowedNetworkTypesListener == null) {
            AllowedNetworkTypesListener allowedNetworkTypesListener = new AllowedNetworkTypesListener(this.mContext.getMainExecutor());
            this.mAllowedNetworkTypesListener = allowedNetworkTypesListener;
            allowedNetworkTypesListener.setAllowedNetworkTypesListener(new NrPreferenceController$$ExternalSyntheticLambda0(this));
        }
        lifecycle.addObserver(this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$0() {
        if (is5gNetworkTypeChanged() && !this.mIs5gSwitchToggled) {
            refreshPreference();
        }
        this.mIs5gSwitchToggled = false;
    }

    public int getAvailabilityStatus(int i) {
        return Moto5gMenuUtils.isAvailable(this.mContext, this.mConfigManager, this.mSubId) ? 0 : 2;
    }

    public boolean setChecked(boolean z) {
        boolean z2 = Moto5gMenuUtils.set5gEnabled(this.mContext, this.mTelephonyManager, this.mSubId, z);
        this.mIs5gSwitchToggled = z2;
        return z2;
    }

    public boolean isChecked() {
        return Moto5gMenuUtils.get5gEnabled(this.mContext, this.mSubId);
    }

    public boolean is5gNetworkTypeAllowed() {
        return checkSupportedRadioBitmask(this.mTelephonyManager.getAllowedNetworkTypesForReason(2), 524288);
    }

    public boolean is5gNetworkTypeChanged() {
        boolean is5gNetworkTypeAllowed = is5gNetworkTypeAllowed();
        if (this.mIs5gNetworkTypeAllowed == is5gNetworkTypeAllowed) {
            return false;
        }
        this.mIs5gNetworkTypeAllowed = is5gNetworkTypeAllowed;
        return true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        AllowedNetworkTypesListener allowedNetworkTypesListener = this.mAllowedNetworkTypesListener;
        if (allowedNetworkTypesListener != null) {
            allowedNetworkTypesListener.register(this.mContext, this.mSubId);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        AllowedNetworkTypesListener allowedNetworkTypesListener = this.mAllowedNetworkTypesListener;
        if (allowedNetworkTypesListener != null) {
            allowedNetworkTypesListener.unregister(this.mContext, this.mSubId);
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

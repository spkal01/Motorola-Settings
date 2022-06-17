package com.motorola.settings.network.vzw;

import android.content.Context;
import android.content.IntentFilter;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.RadioAccessFamily;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.network.AllowedNetworkTypesListener;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.PreferenceCategoryController;
import com.motorola.settings.network.MotoTelephonyFeature;

public class VzwNwOperatorsPreferenceCategoryController extends PreferenceCategoryController implements LifecycleObserver {
    private static final String LOG_TAG = "VzwNwOperatorsController";
    private AllowedNetworkTypesListener mAllowedNetworkTypesListener;
    private PersistableBundle mCarrierConfig;
    private CarrierConfigManager mCarrierConfigManager;
    private Preference mPreference;
    private int mSubId;

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

    public VzwNwOperatorsPreferenceCategoryController(Context context, String str) {
        super(context, str);
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
    }

    public VzwNwOperatorsPreferenceCategoryController init(Lifecycle lifecycle, int i) {
        this.mSubId = i;
        this.mCarrierConfig = this.mCarrierConfigManager.getConfigForSubId(i);
        if (this.mAllowedNetworkTypesListener == null) {
            AllowedNetworkTypesListener allowedNetworkTypesListener = new AllowedNetworkTypesListener(this.mContext.getMainExecutor());
            this.mAllowedNetworkTypesListener = allowedNetworkTypesListener;
            allowedNetworkTypesListener.setAllowedNetworkTypesListener(new C1938x175f0457(this));
        }
        lifecycle.addObserver(this);
        return this;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$0() {
        updateState(this.mPreference);
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (MotoTelephonyFeature.isVzwWorldPhoneEnabled(this.mCarrierConfig)) {
            int networkTypeFromRaf = RadioAccessFamily.getNetworkTypeFromRaf((int) TelephonyManager.from(this.mContext).createForSubscriptionId(this.mSubId).getAllowedNetworkTypesBitmask());
            Log.d(LOG_TAG, "updateState: " + networkTypeFromRaf);
            if (networkTypeFromRaf == 0 || networkTypeFromRaf == 1 || networkTypeFromRaf == 2 || networkTypeFromRaf == 3 || networkTypeFromRaf == 9 || networkTypeFromRaf == 12 || networkTypeFromRaf == 26) {
                preference.setEnabled(true);
            } else {
                preference.setEnabled(false);
            }
        }
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        AllowedNetworkTypesListener allowedNetworkTypesListener;
        if (MotoTelephonyFeature.isVzwWorldPhoneEnabled(this.mCarrierConfig) && (allowedNetworkTypesListener = this.mAllowedNetworkTypesListener) != null) {
            allowedNetworkTypesListener.register(this.mContext, this.mSubId);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        AllowedNetworkTypesListener allowedNetworkTypesListener;
        if (MotoTelephonyFeature.isVzwWorldPhoneEnabled(this.mCarrierConfig) && (allowedNetworkTypesListener = this.mAllowedNetworkTypesListener) != null) {
            allowedNetworkTypesListener.unregister(this.mContext, this.mSubId);
        }
    }
}

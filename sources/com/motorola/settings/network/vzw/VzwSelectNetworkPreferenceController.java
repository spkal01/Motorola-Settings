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
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1978R$array;
import com.android.settings.C1992R$string;
import com.android.settings.network.AllowedNetworkTypesListener;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.network.telephony.TelephonyBasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.utils.ThreadUtils;
import com.motorola.settings.network.MotoTelephonyFeature;

public class VzwSelectNetworkPreferenceController extends TelephonyBasePreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver {
    private static final int AUTOMATIC = 2;
    private static final String LOG_TAG = "VzwSelectNwController";
    private static final int ONLY_2G = 0;
    private static final int ONLY_3G = 1;
    private final String CATEGORY_KEY;
    private AllowedNetworkTypesListener mAllowedNetworkTypesListener;
    private PersistableBundle mCarrierConfig;
    private CarrierConfigManager mCarrierConfigManager;
    private ListPreference mListPreference;
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

    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public VzwSelectNetworkPreferenceController(Context context, String str, String str2) {
        super(context, str);
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        this.CATEGORY_KEY = str2;
    }

    public int getAvailabilityStatus(int i) {
        if (MotoTelephonyFeature.isVzwWorldPhoneEnabled(this.mContext, i) && MobileNetworkUtils.isGsmOptions(this.mContext, i) && !MotoTelephonyFeature.isCdmaLessDevice(this.mContext, i)) {
            return 0;
        }
        return 2;
    }

    public void init(Lifecycle lifecycle, int i) {
        this.mSubId = i;
        this.mTelephonyManager = TelephonyManager.from(this.mContext).createForSubscriptionId(this.mSubId);
        this.mCarrierConfig = this.mCarrierConfigManager.getConfigForSubId(this.mSubId);
        if (this.mAllowedNetworkTypesListener == null) {
            AllowedNetworkTypesListener allowedNetworkTypesListener = new AllowedNetworkTypesListener(this.mContext.getMainExecutor());
            this.mAllowedNetworkTypesListener = allowedNetworkTypesListener;
            allowedNetworkTypesListener.setAllowedNetworkTypesListener(new VzwSelectNetworkPreferenceController$$ExternalSyntheticLambda0(this));
        }
        lifecycle.addObserver(this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$init$0() {
        updateState(this.mListPreference);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mListPreference = (ListPreference) preferenceScreen.findPreference(getPreferenceKey());
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(this.CATEGORY_KEY);
        if (preferenceCategory != null && this.mListPreference == null) {
            ListPreference listPreference = new ListPreference(this.mContext);
            this.mListPreference = listPreference;
            listPreference.setKey(getPreferenceKey());
            ListPreference listPreference2 = this.mListPreference;
            int i = C1992R$string.select_network;
            listPreference2.setTitle(i);
            this.mListPreference.setDialogTitle(i);
            this.mListPreference.setPersistent(false);
            this.mListPreference.setEntries(C1978R$array.select_network_display_values);
            this.mListPreference.setEntryValues(C1978R$array.select_network_values);
            preferenceCategory.addPreference(this.mListPreference);
        }
        super.displayPreference(preferenceScreen);
    }

    public void updateState(Preference preference) {
        int i;
        super.updateState(preference);
        ListPreference listPreference = (ListPreference) preference;
        int networkTypeFromRaf = RadioAccessFamily.getNetworkTypeFromRaf((int) this.mTelephonyManager.getAllowedNetworkTypesBitmask());
        int i2 = 2;
        if (networkTypeFromRaf == 1) {
            i2 = 0;
            i = C1992R$string.select_network_2g_summary;
        } else if (networkTypeFromRaf != 2) {
            i = C1992R$string.select_network_automatic_summary;
        } else {
            i = C1992R$string.select_network_3g_summary;
            i2 = 1;
        }
        listPreference.setValue(Integer.toString(i2));
        listPreference.setSummary(i);
        updateStateBasedOnNwMode(preference, networkTypeFromRaf);
    }

    private void updateStateBasedOnNwMode(Preference preference, int i) {
        Log.d(LOG_TAG, "updateStateBasedOnNwMode: " + i);
        if (!MotoTelephonyFeature.isVzwWorldPhoneEnabled(this.mCarrierConfig)) {
            return;
        }
        if (i == 0 || i == 1 || i == 2 || i == 3 || i == 9 || i == 12 || i == 26) {
            preference.setEnabled(true);
        } else {
            preference.setEnabled(false);
        }
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

    public boolean onPreferenceChange(Preference preference, Object obj) {
        Log.d(LOG_TAG, "onPreferenceChange to: " + obj);
        if (!MotoTelephonyFeature.isVzwWorldPhoneEnabled(this.mCarrierConfig)) {
            return false;
        }
        int parseInt = Integer.parseInt((String) obj);
        int i = 2;
        if (parseInt == 0) {
            Log.d(LOG_TAG, "2G_ONLY/GSM");
            i = 1;
        } else if (parseInt == 1) {
            Log.d(LOG_TAG, "3G_ONLY/UMTS");
        } else if (parseInt != 2) {
            Log.e(LOG_TAG, "onPreferenceChange : unknown item " + obj);
            return false;
        } else {
            Log.d(LOG_TAG, "AUTOMATIC/LTE&GSM&UMTS");
            i = 9;
        }
        ThreadUtils.postOnBackgroundThread((Runnable) new VzwSelectNetworkPreferenceController$$ExternalSyntheticLambda1(this, i));
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPreferenceChange$1(int i) {
        this.mTelephonyManager.setAllowedNetworkTypesForReason(0, (long) RadioAccessFamily.getRafFromNetworkType(i));
        Log.d(LOG_TAG, "onPreferenceChange : doInBackground - subId:" + this.mSubId + ", settingsMode:" + i);
    }
}

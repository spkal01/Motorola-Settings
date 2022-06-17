package com.android.settings.network.telephony;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.PersistableBundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.telephony.ims.ImsMmTelManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.motorola.settings.network.cache.MotoMnsCache;
import com.motorola.settings.wifi.calling.WifiCallingUtils;

public class WifiCallingPreferenceController extends TelephonyBasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private static final String TAG = "WifiCallingPreference";
    Integer mCallState;
    CarrierConfigManager mCarrierConfigManager;
    private ImsMmTelManager mImsMmTelManager;
    /* access modifiers changed from: private */
    public Preference mPreference;
    PhoneAccountHandle mSimCallManager;
    private PhoneTelephonyCallback mTelephonyCallback = new PhoneTelephonyCallback();

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

    public WifiCallingPreferenceController(Context context, String str) {
        super(context, str);
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
    }

    public int getAvailabilityStatus(int i) {
        if (WifiCallingUtils.hasCustomWFCSetting(this.mContext, Integer.valueOf(i))) {
            return 2;
        }
        return (!SubscriptionManager.isValidSubscriptionId(i) || !MobileNetworkUtils.isWifiCallingEnabled(this.mContext, i, (WifiCallingQueryImsState) null, (PhoneAccountHandle) null)) ? 3 : 0;
    }

    public void onStart() {
        this.mTelephonyCallback.register(this.mContext, this.mSubId);
    }

    public void onStop() {
        this.mTelephonyCallback.unregister();
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = findPreference;
        Intent intent = findPreference.getIntent();
        if (intent != null) {
            intent.putExtra("android.provider.extra.SUB_ID", this.mSubId);
        }
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mCallState == null || preference == null) {
            Log.d(TAG, "Skip update under mCallState=" + this.mCallState);
            return;
        }
        CharSequence charSequence = null;
        PhoneAccountHandle phoneAccountHandle = this.mSimCallManager;
        boolean z = false;
        if (phoneAccountHandle != null) {
            Intent buildPhoneAccountConfigureIntent = MobileNetworkUtils.buildPhoneAccountConfigureIntent(this.mContext, phoneAccountHandle);
            if (buildPhoneAccountConfigureIntent != null) {
                PackageManager packageManager = this.mContext.getPackageManager();
                preference.setTitle(packageManager.queryIntentActivities(buildPhoneAccountConfigureIntent, 0).get(0).loadLabel(packageManager));
                preference.setIntent(buildPhoneAccountConfigureIntent);
            } else {
                return;
            }
        } else {
            preference.setTitle((CharSequence) MotoMnsCache.getIns(this.mContext).getResourcesForSubId(this.mSubId).getString(C1992R$string.wifi_calling_settings_title));
            charSequence = getResourceIdForWfcMode(this.mSubId);
        }
        preference.setSummary(charSequence);
        if (this.mCallState.intValue() == 0) {
            z = true;
        }
        preference.setEnabled(z);
    }

    public CharSequence getResourceIdForWfcMode(int i) {
        int i2;
        boolean z;
        int i3;
        int i4;
        PersistableBundle carrierConfigForSubId;
        boolean z2 = false;
        if (queryImsState(i).isEnabledByUser()) {
            if (this.mCarrierConfigManager == null || (carrierConfigForSubId = MotoMnsCache.getIns(this.mContext).getCarrierConfigForSubId(i)) == null) {
                z = false;
            } else {
                z2 = carrierConfigForSubId.getBoolean("use_wfc_home_network_mode_in_roaming_network_bool");
                z = carrierConfigForSubId.getBoolean("moto_hide_wfc_mode_summary");
            }
            if (!getTelephonyManager(this.mContext, i).isNetworkRoaming() || z2) {
                i3 = this.mImsMmTelManager.getVoWiFiModeSetting();
            } else {
                i3 = this.mImsMmTelManager.getVoWiFiRoamingModeSetting();
            }
            if (i3 == 0) {
                i4 = 17041681;
            } else if (i3 == 1) {
                i4 = 17041679;
            } else if (i3 == 2) {
                i4 = 17041682;
            } else if (i3 != 10) {
                z2 = z;
            } else {
                i4 = 17041680;
            }
            int i5 = i4;
            z2 = z;
            i2 = i5;
            if (z2 || i2 == 17041712) {
                return MotoMnsCache.getIns(this.mContext).getResourcesForSubId(i).getText(i2);
            }
            Log.d(TAG, "Hiding WFC Mode Summary");
            return null;
        }
        i2 = 17041712;
        if (z2) {
        }
        return MotoMnsCache.getIns(this.mContext).getResourcesForSubId(i).getText(i2);
    }

    public WifiCallingPreferenceController init(int i) {
        this.mSubId = i;
        this.mImsMmTelManager = getImsMmTelManager(i);
        this.mSimCallManager = ((TelecomManager) this.mContext.getSystemService(TelecomManager.class)).getSimCallManagerForSubscription(this.mSubId);
        return this;
    }

    /* access modifiers changed from: package-private */
    public WifiCallingQueryImsState queryImsState(int i) {
        return new WifiCallingQueryImsState(this.mContext, i);
    }

    /* access modifiers changed from: protected */
    public ImsMmTelManager getImsMmTelManager(int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return null;
        }
        return ImsMmTelManager.createForSubscriptionId(i);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x000f, code lost:
        r1 = r0.createForSubscriptionId(r2);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.telephony.TelephonyManager getTelephonyManager(android.content.Context r1, int r2) {
        /*
            r0 = this;
            java.lang.Class<android.telephony.TelephonyManager> r0 = android.telephony.TelephonyManager.class
            java.lang.Object r0 = r1.getSystemService(r0)
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0
            boolean r1 = android.telephony.SubscriptionManager.isValidSubscriptionId(r2)
            if (r1 != 0) goto L_0x000f
            return r0
        L_0x000f:
            android.telephony.TelephonyManager r1 = r0.createForSubscriptionId(r2)
            if (r1 != 0) goto L_0x0016
            goto L_0x0017
        L_0x0016:
            r0 = r1
        L_0x0017:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.telephony.WifiCallingPreferenceController.getTelephonyManager(android.content.Context, int):android.telephony.TelephonyManager");
    }

    private class PhoneTelephonyCallback extends TelephonyCallback implements TelephonyCallback.CallStateListener {
        private TelephonyManager mTelephonyManager;

        private PhoneTelephonyCallback() {
        }

        public void onCallStateChanged(int i) {
            WifiCallingPreferenceController.this.mCallState = Integer.valueOf(i);
            WifiCallingPreferenceController wifiCallingPreferenceController = WifiCallingPreferenceController.this;
            wifiCallingPreferenceController.updateState(wifiCallingPreferenceController.mPreference);
        }

        public void register(Context context, int i) {
            TelephonyManager telephonyManager = WifiCallingPreferenceController.this.getTelephonyManager(context, i);
            this.mTelephonyManager = telephonyManager;
            WifiCallingPreferenceController.this.mCallState = Integer.valueOf(telephonyManager.getCallState(i));
            this.mTelephonyManager.registerTelephonyCallback(context.getMainExecutor(), this);
        }

        public void unregister() {
            WifiCallingPreferenceController.this.mCallState = null;
            this.mTelephonyManager.unregisterTelephonyCallback(this);
        }
    }
}

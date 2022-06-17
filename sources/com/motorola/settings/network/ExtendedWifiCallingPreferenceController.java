package com.motorola.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C1992R$string;
import com.android.settings.network.ims.VolteQueryImsState;
import com.android.settings.network.telephony.WifiCallingPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.List;

public class ExtendedWifiCallingPreferenceController extends WifiCallingPreferenceController {
    private static final Intent INTENT_SIMPLE_WIFI_CALLING_SETTINGS = new Intent("com.motorola.wfc.SIMPLE_WFC_SETTING");
    private static final Intent INTENT_SIMPLE_WIFI_CALLING_SETTINGS_SLOT1 = new Intent("com.motorola.wfc.SIMPLE_WFC_SETTING_SLOT1");
    private static final String TAG = "ExtendedWifiCallingPreferenceController";
    private boolean mListeningWfcPackageChanged = false;
    private BroadcastReceiver mPackageChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ExtendedWifiCallingPreferenceController extendedWifiCallingPreferenceController = ExtendedWifiCallingPreferenceController.this;
            extendedWifiCallingPreferenceController.updateState(extendedWifiCallingPreferenceController.mWFCPref);
        }
    };
    protected Preference mWFCPref;

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

    public ExtendedWifiCallingPreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus(int i) {
        if (hasSimpleWFCSetting()) {
            return 0;
        }
        return super.getAvailabilityStatus(i);
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mWFCPref = preferenceScreen.findPreference(getPreferenceKey());
    }

    public void onStart() {
        super.onStart();
        String packageForCustomWfc = getPackageForCustomWfc();
        if (packageForCustomWfc != null) {
            this.mListeningWfcPackageChanged = true;
            IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_CHANGED");
            intentFilter.addDataScheme("package");
            intentFilter.addDataSchemeSpecificPart(packageForCustomWfc, 0);
            this.mContext.registerReceiver(this.mPackageChangedReceiver, intentFilter);
        }
    }

    public void onStop() {
        super.onStop();
        if (this.mListeningWfcPackageChanged) {
            this.mContext.unregisterReceiver(this.mPackageChangedReceiver);
        }
        this.mListeningWfcPackageChanged = false;
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        if (hasSimpleWFCSetting()) {
            boolean isVoLteProvisioned = new VolteQueryImsState(this.mContext, Integer.MAX_VALUE).isVoLteProvisioned();
            Preference preference2 = this.mWFCPref;
            int i = C1992R$string.pref_wifi_calling;
            preference2.setTitle(i);
            setWfcSummary(isVoLteProvisioned);
            this.mWFCPref.setTitle(i);
            this.mWFCPref.setEnabled(isVoLteProvisioned);
            this.mWFCPref.setIntent(getSimpleWFCIntent());
        }
    }

    private void setWfcSummary(boolean z) {
        if (MotoTelephonyFeature.isUscCarrier(this.mContext, this.mSubId)) {
            CharSequence resourceIdForWfcMode = getResourceIdForWfcMode(this.mSubId);
            if (!TextUtils.isEmpty(resourceIdForWfcMode)) {
                this.mWFCPref.setSummary(resourceIdForWfcMode);
                return;
            }
        }
        this.mWFCPref.setSummary(z ? C1992R$string.pref_wifi_summary : C1992R$string.pref_wifi_summary_volte_off);
    }

    private Intent getSimpleWFCIntent() {
        return SubscriptionManager.getSlotIndex(this.mSubId) == 1 ? INTENT_SIMPLE_WIFI_CALLING_SETTINGS_SLOT1 : INTENT_SIMPLE_WIFI_CALLING_SETTINGS;
    }

    private boolean hasSimpleWFCSetting() {
        Intent simpleWFCIntent = getSimpleWFCIntent();
        List<ResolveInfo> queryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(simpleWFCIntent, 1048576);
        boolean z = queryIntentActivities != null && queryIntentActivities.size() > 0;
        MotoMnsLog.logd(TAG, "hasSimpleWFCSetting = " + z + " intent = " + simpleWFCIntent);
        return z;
    }

    private String getPackageForCustomWfc() {
        List<ResolveInfo> queryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(getSimpleWFCIntent(), 66048);
        if (queryIntentActivities == null) {
            return null;
        }
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null) {
                return activityInfo.packageName;
            }
        }
        return null;
    }
}

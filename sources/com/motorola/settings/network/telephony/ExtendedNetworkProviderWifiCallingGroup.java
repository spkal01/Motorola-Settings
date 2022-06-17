package com.motorola.settings.network.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.telephony.SubscriptionManager;
import com.android.settings.C1992R$string;
import com.android.settings.network.ims.VolteQueryImsState;
import com.android.settings.network.telephony.NetworkProviderWifiCallingGroup;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.motorola.settings.network.MotoMnsLog;
import com.motorola.settings.network.MotoTelephonyFeature;
import com.motorola.settings.wifi.calling.WifiCallingUtils;
import java.util.List;
import java.util.Set;

public class ExtendedNetworkProviderWifiCallingGroup extends NetworkProviderWifiCallingGroup implements OnStop {
    private static final Intent INTENT_SIMPLE_WIFI_CALLING_SETTINGS = new Intent("com.motorola.wfc.SIMPLE_WFC_SETTING");
    private static final Intent INTENT_SIMPLE_WIFI_CALLING_SETTINGS_SLOT1 = new Intent("com.motorola.wfc.SIMPLE_WFC_SETTING_SLOT1");
    private boolean mListeningWfcPackageChanged = false;
    private BroadcastReceiver mPackageChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ExtendedNetworkProviderWifiCallingGroup.this.update();
        }
    };

    public ExtendedNetworkProviderWifiCallingGroup(Context context, Lifecycle lifecycle, String str) {
        super(context, lifecycle, str);
    }

    /* access modifiers changed from: protected */
    public void setSubscriptionInfoList(Context context) {
        super.setSubscriptionInfoList(context);
        setCustomWfcPackageListner(this.mSubIdList);
    }

    /* access modifiers changed from: protected */
    public Intent getWfcIntent(int i) {
        if (WifiCallingUtils.hasCustomWFCSetting(this.mContext, Integer.valueOf(i))) {
            return getCustomWFCIntent(i);
        }
        return super.getWfcIntent(i);
    }

    private void setCustomWfcPackageListner(Set<Integer> set) {
        if (this.mListeningWfcPackageChanged) {
            this.mContext.unregisterReceiver(this.mPackageChangedReceiver);
            this.mListeningWfcPackageChanged = false;
        }
        if (set != null && set.size() >= 1) {
            IntentFilter intentFilter = null;
            for (Integer intValue : set) {
                String packageForCustomWfc = getPackageForCustomWfc(intValue.intValue());
                if (packageForCustomWfc != null) {
                    if (!this.mListeningWfcPackageChanged && intentFilter == null) {
                        this.mListeningWfcPackageChanged = true;
                        intentFilter = new IntentFilter("android.intent.action.PACKAGE_CHANGED");
                        intentFilter.addDataScheme("package");
                    }
                    intentFilter.addDataSchemeSpecificPart(packageForCustomWfc, 0);
                }
            }
            if (intentFilter != null) {
                this.mContext.registerReceiver(this.mPackageChangedReceiver, intentFilter);
            }
        }
    }

    public void onStop() {
        if (this.mListeningWfcPackageChanged) {
            this.mContext.unregisterReceiver(this.mPackageChangedReceiver);
        }
        this.mListeningWfcPackageChanged = false;
    }

    /* access modifiers changed from: protected */
    public int getCustomWfcPrefSummaryResId(int i, int i2) {
        int i3;
        if (!MotoTelephonyFeature.isFtr5600Enabled(this.mContext, i) || !hasSimpleWFCSetting(i)) {
            return i2;
        }
        if (new VolteQueryImsState(this.mContext, Integer.MAX_VALUE).isVoLteProvisioned()) {
            i3 = C1992R$string.pref_wifi_summary;
        } else {
            i3 = C1992R$string.pref_wifi_summary_volte_off;
        }
        return i3;
    }

    private Intent getCustomWFCIntent(int i) {
        Intent intent;
        int slotIndex = SubscriptionManager.getSlotIndex(i);
        if (MotoTelephonyFeature.isFtr5013Enabled(this.mContext, i)) {
            intent = new Intent(slotIndex == 1 ? "com.motorola.settings.OVERRIDE_AOSP_WIFI_CALLING_SETTINGS_SLOT1" : "com.motorola.settings.OVERRIDE_AOSP_WIFI_CALLING_SETTINGS");
        } else if (MotoTelephonyFeature.isFtr6252Enabled(this.mContext, i)) {
            intent = new Intent(slotIndex == 1 ? "com.motorola.carriersettingsext.WFC_SLOT1" : "com.motorola.carriersettingsext.WFC");
        } else {
            intent = slotIndex == 1 ? INTENT_SIMPLE_WIFI_CALLING_SETTINGS_SLOT1 : INTENT_SIMPLE_WIFI_CALLING_SETTINGS;
        }
        intent.putExtra("android.telephony.extra.SUBSCRIPTION_INDEX", i);
        return intent;
    }

    private boolean hasSimpleWFCSetting(int i) {
        Intent customWFCIntent = getCustomWFCIntent(i);
        List<ResolveInfo> queryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(customWFCIntent, 1048576);
        boolean z = queryIntentActivities != null && queryIntentActivities.size() > 0;
        MotoMnsLog.logd("ExtendedNetworkProviderWifiCallingGroup", "hasSimpleWFCSetting = " + z + " intent = " + customWFCIntent);
        return z;
    }

    private String getPackageForCustomWfc(int i) {
        List<ResolveInfo> queryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(getCustomWFCIntent(i), 66048);
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

    /* access modifiers changed from: protected */
    public boolean shouldShowWifiCallingForSub(int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i) || !WifiCallingUtils.hasCustomWFCSetting(this.mContext, Integer.valueOf(i))) {
            return super.shouldShowWifiCallingForSub(i);
        }
        return true;
    }
}

package com.motorola.settingslib.moto5gmenu;

import android.content.Context;
import android.telephony.CarrierConfigManager;
import android.telephony.RadioAccessFamily;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.settingslib.utils.ThreadUtils;

public class Moto5gMenuUtils {
    private static final String TAG = "Moto5gMenuUtils";

    public static boolean isAvailable(Context context, CarrierConfigManager carrierConfigManager, int i) {
        return is5gMenuNeeded(context, i) && Moto5gNetworkUtils.modemSupports5g(context, i) && Moto5gNetworkUtils.carrierSupports5gByCarrierConfig(context, carrierConfigManager, i) && (Moto5gNetworkUtils.isDds(i) || Moto5gNetworkUtils.secondarySimSupportsUpTo5g());
    }

    public static boolean set5gEnabled(Context context, TelephonyManager telephonyManager, int i, boolean z) {
        int i2 = z ? 27 : 10;
        long allowedNetworkTypesForReason = telephonyManager.getAllowedNetworkTypesForReason(2);
        long j = z ? allowedNetworkTypesForReason | 524288 : allowedNetworkTypesForReason & -524289;
        telephonyManager.setAllowedNetworkTypesForReason(2, j);
        int pnt = Moto5gNetworkUtils.getPnt(context, i);
        String str = TAG;
        Log.d(str, "newType:" + i2 + ", currentPnt:" + pnt + ", allowRaf:" + j + ", is5gEnabled:" + z);
        if (Moto5gNetworkUtils.isSameHighestRat(pnt, i2)) {
            return true;
        }
        if (z || Moto5gNetworkUtils.is5g(pnt)) {
            setAllowedNetworkTypesForReason(telephonyManager, i, i2);
        }
        return true;
    }

    public static boolean get5gEnabled(Context context, int i) {
        return Moto5gNetworkUtils.carrierAllowed5g(context, i);
    }

    protected static void setAllowedNetworkTypesForReason(TelephonyManager telephonyManager, int i, int i2) {
        ThreadUtils.postOnBackgroundThread((Runnable) new Moto5gMenuUtils$$ExternalSyntheticLambda0(telephonyManager, i2, i));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$setAllowedNetworkTypesForReason$0(TelephonyManager telephonyManager, int i, int i2) {
        telephonyManager.setAllowedNetworkTypesForReason(0, (long) RadioAccessFamily.getRafFromNetworkType(i));
        String str = TAG;
        Log.d(str, "setAllowedNetworkTypesForReason doInBackground - subId:" + i2 + ", networkType:" + i);
    }

    public static boolean is5gMenuNeeded(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i) || !isResEnabled(context, i, R$bool.config_is_5g_menu_needed)) {
            return false;
        }
        String str = TAG;
        Log.d(str, "is5gMenuNeeded: true, subId: " + i);
        return true;
    }

    private static boolean isResEnabled(Context context, int i, int i2) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return false;
        }
        return SubscriptionManager.getResourcesForSubId(context, i).getBoolean(i2);
    }
}

package com.motorola.settingslib.moto5gmenu;

import android.content.Context;
import android.telephony.CarrierConfigManager;
import android.telephony.RadioAccessFamily;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.telephony.util.ArrayUtils;

public class Moto5gNetworkUtils {
    private static final String TAG = "Moto5gNetworkUtils";

    public static boolean modemSupports5g(Context context, int i) {
        long supportedRadioAccessFamily = TelephonyManager.from(context).createForSubscriptionId(i).getSupportedRadioAccessFamily();
        boolean z = (524288 & supportedRadioAccessFamily) > 0;
        String str = TAG;
        Log.d(str, "modemSupports5g: " + z + " supportedRadioBitmask: " + supportedRadioAccessFamily);
        return z;
    }

    public static boolean carrierSupports5gByCarrierConfig(Context context, CarrierConfigManager carrierConfigManager, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return false;
        }
        boolean z = !ArrayUtils.isEmpty(carrierConfigManager.getConfigForSubId(i).getIntArray("carrier_nr_availabilities_int_array"));
        Log.d(TAG, "carrierSupports5gByCarrierConfig: " + z);
        return z;
    }

    public static boolean isDds(int i) {
        return SubscriptionManager.getDefaultDataSubscriptionId() == i;
    }

    public static boolean secondarySimSupportsUpTo5g() {
        int defaultNetworkModeForSecondarySim = getDefaultNetworkModeForSecondarySim();
        boolean z = getHighestRafCapability(defaultNetworkModeForSecondarySim) == 4;
        String str = TAG;
        Log.d(str, "defaultModeSecondarySim: " + defaultNetworkModeForSecondarySim + ", secondarySimSupportsUpTo5g:" + z);
        return z;
    }

    private static int getDefaultNetworkModeForSecondarySim() {
        return Integer.parseInt(TelephonyManager.getTelephonyProperty(1, "ro.telephony.default_network", Integer.toString(1)));
    }

    public static boolean is5g(int i) {
        return getHighestRafCapability(i) == 4;
    }

    public static boolean isSameHighestRat(int i, int i2) {
        return getHighestRafCapability(i) == getHighestRafCapability(i2);
    }

    private static int getHighestRafCapability(int i) {
        int rafFromNetworkType = RadioAccessFamily.getRafFromNetworkType(i);
        if ((524288 & rafFromNetworkType) > 0) {
            return 4;
        }
        if ((266240 & rafFromNetworkType) > 0) {
            return 3;
        }
        if ((93108 & rafFromNetworkType) > 0) {
            return 2;
        }
        return (rafFromNetworkType & 32843) > 0 ? 1 : 0;
    }

    public static boolean carrierAllowed5g(Context context, int i) {
        boolean z = (TelephonyManager.from(context).createForSubscriptionId(i).getAllowedNetworkTypesForReason(2) & 524288) > 0;
        String str = TAG;
        Log.d(str, "carrierAllowed5g: " + z);
        return z;
    }

    public static int getPnt(Context context, int i) {
        return RadioAccessFamily.getNetworkTypeFromRaf((int) TelephonyManager.from(context).createForSubscriptionId(i).getAllowedNetworkTypesForReason(0));
    }
}

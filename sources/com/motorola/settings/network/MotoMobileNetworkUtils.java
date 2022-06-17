package com.motorola.settings.network;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.android.ims.ImsManager;
import com.android.internal.telephony.util.ArrayUtils;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.motorola.android.telephony.MotoExtTelephonyManager;
import com.motorola.settings.network.cache.MotoMnsCache;
import java.util.List;

public class MotoMobileNetworkUtils {
    public static boolean isAirplaneModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    public static boolean is3G(int i) {
        return getHighestRafCapability(i) == 2;
    }

    public static boolean is2G(int i) {
        return getHighestRafCapability(i) == 1;
    }

    public static boolean isSameHighestRat(int i, int i2) {
        return getHighestRafCapability(i) == getHighestRafCapability(i2);
    }

    private static int getHighestRafCapability(int i) {
        int rafFromNetworkType = (int) MobileNetworkUtils.getRafFromNetworkType(i);
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

    public static boolean modemSupports5g(Context context, int i) {
        return (TelephonyManager.from(context).createForSubscriptionId(i).getSupportedRadioAccessFamily() & 524288) > 0;
    }

    public static boolean simcardSupports5gOption(boolean z, boolean z2, Context context, int i) {
        boolean z3 = true;
        if (!z || !z2 || (!isDds(i) && !secondarySimSupportsUpTo5g())) {
            z3 = false;
        }
        MotoMnsLog.logd("MotoMobileNetworkUtils", "simcardSupports5gOption: " + z3);
        return z3;
    }

    public static boolean carrierSupports5gByCarrierConfig(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return false;
        }
        boolean z = !ArrayUtils.isEmpty(MotoMnsCache.getIns(context).getCarrierConfigForSubId(i).getIntArray("carrier_nr_availabilities_int_array"));
        MotoMnsLog.logd("MotoMobileNetworkUtils", "carrierSupports5gByCarrierConfig: " + z);
        return z;
    }

    public static boolean carrierAllowed5g(Context context, int i) {
        boolean z = (TelephonyManager.from(context).createForSubscriptionId(i).getAllowedNetworkTypesForReason(2) & 524288) > 0;
        MotoMnsLog.logd("MotoMobileNetworkUtils", "carrierAllowed5g: " + z);
        return z;
    }

    public static int getPnt(Context context, int i) {
        return MobileNetworkUtils.getNetworkTypeFromRaf((int) TelephonyManager.from(context).createForSubscriptionId(i).getAllowedNetworkTypesForReason(0));
    }

    private static int getDefaultNetworkModeForSecondarySim() {
        return Integer.parseInt(TelephonyManager.getTelephonyProperty(1, "ro.telephony.default_network", Integer.toString(1)));
    }

    public static CharSequence getMotoDisplayName(SubscriptionInfo subscriptionInfo, Context context) {
        CharSequence uniqueSubscriptionDisplayName = SubscriptionUtil.getUniqueSubscriptionDisplayName(Integer.valueOf(subscriptionInfo.getSubscriptionId()), context);
        if (subscriptionInfo.isEmbedded()) {
            return uniqueSubscriptionDisplayName;
        }
        return (isSimInSlotUnlocked(context, subscriptionInfo.getSubscriptionId()) || !isSimActive(context, subscriptionInfo.getSubscriptionId())) ? uniqueSubscriptionDisplayName : getDefaultSubscriptionName(context, subscriptionInfo.getSimSlotIndex());
    }

    public static boolean isSimActive(Context context, int i) {
        boolean isSimProvisioned = isSimProvisioned(context, i);
        SubscriptionInfo activeSubInfo = MotoMnsCache.getIns(context).getActiveSubInfo(i);
        if (activeSubInfo == null || activeSubInfo.isEmbedded()) {
            boolean isEsimProfileEnabled = isEsimProfileEnabled(context, i);
            MotoMnsLog.logd("MotoMobileNetworkUtils", "isSimActive - simProvisioned:" + isSimProvisioned + ", esimProfileEnabled:" + isEsimProfileEnabled);
            return isSimProvisioned && isEsimProfileEnabled;
        }
        MotoMnsLog.logd("MotoMobileNetworkUtils", "isSimActive - simProvisioned:" + isSimProvisioned);
        return isSimProvisioned;
    }

    public static boolean isSimProvisioned(Context context, int i) {
        return getCurrentUiccCardProvisioningStatus(context, i) == 1;
    }

    public static boolean isEsimProfileEnabled(Context context, int i) {
        SubscriptionInfo activeSubInfo = MotoMnsCache.getIns(context).getActiveSubInfo(i);
        if (activeSubInfo == null || !activeSubInfo.isEmbedded()) {
            return false;
        }
        return ((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).isActiveSubscriptionId(i);
    }

    public static final int getCurrentUiccCardProvisioningStatus(Context context, int i) {
        return new MotoExtTelephonyManager(context).getCurrentUiccCardProvisioningStatus(i);
    }

    public static boolean isSimInSlotUnlocked(Context context, int i) {
        boolean z;
        TelephonyManager from;
        int slotIndex = SubscriptionManager.getSlotIndex(i);
        if (hasSimInSlot(context, slotIndex) && (from = TelephonyManager.from(context)) != null) {
            int simState = from.getSimState(slotIndex);
            MotoMnsLog.logd("MotoMobileNetworkUtils", "simState - " + simState);
            if (!(simState == 2 || simState == 3 || simState == 4)) {
                z = true;
                MotoMnsLog.logd("MotoMobileNetworkUtils", "isSimInSlotUnlocked - " + z);
                return z;
            }
        }
        z = false;
        MotoMnsLog.logd("MotoMobileNetworkUtils", "isSimInSlotUnlocked - " + z);
        return z;
    }

    public static boolean hasSimInSlot(Context context, int i) {
        TelephonyManager from = TelephonyManager.from(context);
        boolean hasIccCard = from != null ? from.hasIccCard(i) : false;
        MotoMnsLog.logd("MotoMobileNetworkUtils", "hasSimInSlot - " + hasIccCard);
        return hasIccCard;
    }

    public static boolean isSimInSlotUsable(Context context, int i) {
        return isSimActive(context, i) && isSimInSlotUnlocked(context, i);
    }

    public static String getDefaultSubscriptionName(Context context, int i) {
        if (i == 0) {
            return context.getString(17041544);
        }
        return context.getString(17041545);
    }

    public static boolean secondarySimSupportsUpTo5g() {
        int defaultNetworkModeForSecondarySim = getDefaultNetworkModeForSecondarySim();
        boolean z = getHighestRafCapability(defaultNetworkModeForSecondarySim) == 4;
        MotoMnsLog.logd("MotoMobileNetworkUtils", "defaultModeSecondarySim: " + defaultNetworkModeForSecondarySim + ", secondarySimSupportsUpTo5g:" + z);
        return z;
    }

    public static boolean secondarySimSupportsUpTo3g() {
        int defaultNetworkModeForSecondarySim = getDefaultNetworkModeForSecondarySim();
        boolean z = getHighestRafCapability(defaultNetworkModeForSecondarySim) == 2;
        MotoMnsLog.logd("MotoMobileNetworkUtils", "defaultModeSecondarySim: " + defaultNetworkModeForSecondarySim + ", secondarySimLimitedTo3g:" + z);
        return z;
    }

    public static boolean isDds(int i) {
        return SubscriptionManager.getDefaultDataSubscriptionId() == i;
    }

    public static boolean isVolteSupportedByModem(Context context, int i) {
        int phoneId = MotoMnsCache.getIns(context).getPhoneId(i);
        if (!SubscriptionManager.isValidPhoneId(phoneId)) {
            return false;
        }
        TelephonyManager createForSubscriptionId = TelephonyManager.from(context).createForSubscriptionId(i);
        if (createForSubscriptionId == null || !createForSubscriptionId.isImsRegistered()) {
            boolean isVolteSupportedByModem = ImsManager.getInstance(context.getApplicationContext(), phoneId).isVolteSupportedByModem();
            MotoMnsLog.logd("MotoMobileNetworkUtils", "isVolteSupportedByModem:" + isVolteSupportedByModem);
            return isVolteSupportedByModem;
        }
        MotoMnsLog.logd("MotoMobileNetworkUtils", "isVolteSupportedByModem: true, Ims is registered now");
        return true;
    }

    public static boolean isPackageEnabled(Context context, String str, boolean z) {
        boolean z2 = false;
        try {
            z2 = context.getPackageManager().getApplicationInfo(str, z ? 1048576 : 0).enabled;
        } catch (PackageManager.NameNotFoundException unused) {
        }
        MotoMnsLog.logd("MotoMobileNetworkUtils", "packageName:" + str + ", packageEnabled:" + z2);
        return z2;
    }

    public static boolean isComponentEnabled(Context context, String str, boolean z) {
        boolean z2 = false;
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(new Intent(str), z ? 1048576 : 0);
        if (queryIntentActivities != null && queryIntentActivities.size() > 0) {
            z2 = true;
        }
        MotoMnsLog.logd("MotoMobileNetworkUtils", "actionName:" + str + ", enabled:" + z2);
        return z2;
    }

    public static Intent getIntentResolvedToSystemActivity(Context context, String str) {
        if (context == null || TextUtils.isEmpty(str)) {
            return null;
        }
        Intent intent = new Intent(str);
        for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentActivities(intent, 0)) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if ((activityInfo.applicationInfo.flags & 1) != 0) {
                return intent.setClassName(activityInfo.packageName, activityInfo.name);
            }
        }
        return null;
    }

    public static boolean isSimConfigurable(Context context, int i) {
        if (((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfo(i) == null) {
            return false;
        }
        boolean isSimInSlotUsable = isSimInSlotUsable(context, i);
        boolean isConfigForIdentifiedCarrier = CarrierConfigManager.isConfigForIdentifiedCarrier(((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).getConfigForSubId(i));
        boolean isAirplaneModeOn = isAirplaneModeOn(context);
        MotoMnsLog.logd("MotoMobileNetworkUtils", "isSimInSlotUsable: " + isSimInSlotUsable + ", isCarrierConfigLoaded:" + isConfigForIdentifiedCarrier + ", isAirplaneMode:" + isAirplaneModeOn);
        if (!isSimInSlotUsable || !isConfigForIdentifiedCarrier || isAirplaneModeOn) {
            return false;
        }
        return true;
    }

    public static boolean isSecondarySimSupportSA(Context context) {
        try {
            boolean z = context.getResources().getBoolean(17891598);
            MotoMnsLog.logd("MotoMobileNetworkUtils", "isSecondarySimSupportSA: " + z);
            return z;
        } catch (Resources.NotFoundException unused) {
            MotoMnsLog.logd("MotoMobileNetworkUtils", "Resource not found: R.bool.config_enable_secondary_sim_5g_sa");
            return false;
        }
    }

    public static boolean getBooleanForApplication(Context context, String str, String str2) {
        try {
            Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(str);
            return resourcesForApplication.getBoolean(resourcesForApplication.getIdentifier(str2, "bool", str));
        } catch (PackageManager.NameNotFoundException unused) {
            MotoMnsLog.logd("MotoMobileNetworkUtils", "Package name not found: " + str);
            return false;
        } catch (Resources.NotFoundException unused2) {
            MotoMnsLog.logd("MotoMobileNetworkUtils", "Resource not found: " + str2);
            return false;
        }
    }

    public static int getCarrier5GOptionDefaultValue(Context context, int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return -1;
        }
        return MotoMnsCache.getIns(context).getCarrierConfigForSubId(i).getInt("moto_user_default_nr_mode");
    }

    public static int setDefaultDataSubIdWithNwAutoSwitch(Context context, int i) {
        return new MotoExtTelephonyManager(context).setDefaultDataSubIdWithNwAutoSwitch(i);
    }
}

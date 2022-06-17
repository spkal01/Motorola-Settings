package com.motorola.settings.deviceinfo;

import android.app.AppGlobals;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.C1978R$array;
import com.android.settings.C1980R$bool;
import com.android.settings.deviceinfo.HardwareInfoPreferenceController;
import com.motorola.android.provider.MotorolaSettings;
import com.motorola.android.telephony.MotoExtTelephonyManager;
import java.util.Arrays;
import java.util.List;

public class DeviceUtils {
    private static final boolean DBG = Build.TYPE.equals("userdebug");

    private static boolean isVZWSim(int i) {
        return i == 1839;
    }

    public static String getDeviceModelSummary() {
        Application initialApplication = AppGlobals.getInitialApplication();
        StringBuilder sb = new StringBuilder();
        sb.append(HardwareInfoPreferenceController.getDeviceModel());
        if (initialApplication.getResources().getBoolean(C1980R$bool.append_serialno_to_model)) {
            sb.append(" ");
            sb.append(Build.getSerial());
        }
        if (appendSkuToModel(initialApplication)) {
            sb.append(" (");
            sb.append(getSku());
            sb.append(")");
        }
        return sb.toString();
    }

    public static String getSku() {
        return SystemProperties.get("ro.boot.hardware.sku");
    }

    private static boolean appendSkuToModel(Context context) {
        String[] stringArray = context.getResources().getStringArray(C1978R$array.channel_ids_needing_sku_appended_to_model);
        String channelId = getChannelId(context);
        if (!TextUtils.isEmpty(channelId)) {
            for (String equals : stringArray) {
                if (channelId.equals(equals)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getChannelId(Context context) {
        return MotorolaSettings.Global.getString(context.getContentResolver(), "channel_id");
    }

    public static String getDeviceDisplayModel() {
        return SystemProperties.get("ro.vendor.product.display", Build.MODEL);
    }

    public static boolean isVzwFtr7071(Context context, int i) {
        SubscriptionInfo activeSubscriptionInfo = SubscriptionManager.from(context).getActiveSubscriptionInfo(i);
        TelephonyManager createForSubscriptionId = TelephonyManager.from(context).createForSubscriptionId(i);
        if (!(activeSubscriptionInfo == null || createForSubscriptionId == null || !isVZWSim(activeSubscriptionInfo.getCarrierId()))) {
            int pco = getPco(context, i);
            if (pco == -1 && createForSubscriptionId.getVoiceActivationState(i) == 3) {
                if (DBG) {
                    Log.d("mDeviceUtils", "LTE reject cause 8. isVzwFtr7071 is now enabled");
                }
                return true;
            } else if (pco == 5) {
                if (DBG) {
                    Log.d("mDeviceUtils", "PCO_5 received. isVzwFtr7071 is now enabled");
                }
                return true;
            }
        }
        return false;
    }

    private static int getPco(Context context, int i) {
        byte[] byteArray;
        Bundle pcoData = new MotoExtTelephonyManager(context, i).getPcoData("default");
        if (pcoData == null || (byteArray = pcoData.getByteArray("android.telephony.extra.PCO_VALUE")) == null || byteArray.length <= 0) {
            return -1;
        }
        if (DBG) {
            Log.d("mDeviceUtils", "Existing PCO " + byteArray[0]);
        }
        return byteArray[0];
    }

    public static String getFormattedImei(Context context, int i) {
        String imei = ((TelephonyManager) context.getSystemService("phone")).getImei(i);
        return (imei == null || !isConfigKeyEnabled(context, "moto_carrier_format_device_info")) ? imei : imei.replaceAll("([0-9]{1,4})", "$1 ").trim();
    }

    public static String getFormattedSimSerialNumber(Context context, int i, int i2) {
        String simSerialNumber = TelephonyManager.from(context).createForSubscriptionId(i).getSimSerialNumber();
        MotoExtTelephonyManager motoExtTelephonyManager = new MotoExtTelephonyManager(context, i);
        PersistableBundle configForSubId = ((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).getConfigForSubId(i);
        if ("boost".equalsIgnoreCase(getChannelId(context)) && configForSubId != null && configForSubId.getBoolean("moto_append_filler_digits_to_iccid") && simSerialNumber != null && simSerialNumber.length() <= 19) {
            simSerialNumber = motoExtTelephonyManager.getFullIccSerialNumber(i2);
        }
        return (simSerialNumber == null || !isConfigKeyEnabled(context, "moto_carrier_format_device_info")) ? simSerialNumber : simSerialNumber.replaceAll("([0-9]{1,4})", "$1 ").trim();
    }

    public static boolean hideMEID(Context context) {
        return isConfigKeyEnabled(context, "moto_carrier_hide_meid");
    }

    public static boolean hideMEIDonCdmaLte(Context context, int i) {
        return isCdmaLteEnabled(context, i) && isConfigKeyEnabled(context, "moto_carrier_format_device_info");
    }

    public static boolean isConfigKeyEnabled(Context context, String str) {
        boolean z = ((CarrierConfigManager) context.getSystemService("carrier_config")).getConfig().getBoolean(str);
        if (DBG) {
            Log.d("mDeviceUtils", str + ", isEnabled: " + z);
        }
        return z;
    }

    private static boolean isCdmaLteEnabled(Context context, int i) {
        SubscriptionInfo subscriptionInfo = getSubscriptionInfo(context, i);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        if (subscriptionInfo == null || telephonyManager.getLteOnCdmaMode(subscriptionInfo.getSubscriptionId()) != 1) {
            return false;
        }
        return true;
    }

    private static SubscriptionInfo getSubscriptionInfo(Context context, int i) {
        List<SubscriptionInfo> activeSubscriptionInfoList = SubscriptionManager.from(context).getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList == null) {
            return null;
        }
        for (SubscriptionInfo next : activeSubscriptionInfoList) {
            if (next.getSimSlotIndex() == i) {
                return next;
            }
        }
        return null;
    }

    public static boolean shouldForceSingleSimInDSDS(Context context) {
        String[] stringArray = context.getResources().getStringArray(C1978R$array.config_carriers_force_single_sim_in_ds);
        if (stringArray.length == 0) {
            return false;
        }
        return Arrays.asList(stringArray).contains(getChannelId(context));
    }
}

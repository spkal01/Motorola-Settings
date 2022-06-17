package com.motorola.settings.wifi.calling;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.settings.network.SubscriptionUtil;
import com.motorola.settings.network.MotoMobileNetworkUtils;
import java.util.List;

public class WifiCallingUtils {
    public static boolean hasCustomWFCSetting(Context context, Integer num) {
        boolean z = true;
        Intent intent = new Intent((num == null || SubscriptionManager.getSlotIndex(num.intValue()) != 1) ? "com.motorola.settings.OVERRIDE_AOSP_WIFI_CALLING_SETTINGS" : "com.motorola.settings.OVERRIDE_AOSP_WIFI_CALLING_SETTINGS_SLOT1");
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 1048576);
        if (queryIntentActivities == null || queryIntentActivities.size() <= 0) {
            z = false;
        }
        Log.d("WifiCallingUtils", "hasCustomWFCSetting: " + z + ", AOSP_OVERRIDE_ACTION:" + intent);
        return z;
    }

    public static boolean hasMotoCustomHelp(Context context, int i) {
        PersistableBundle configForSubId;
        if (i == -1 || (configForSubId = ((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).getConfigForSubId(i)) == null) {
            return false;
        }
        return configForSubId.getBoolean("moto_show_customized_wfc_help_and_dialog_bool", false);
    }

    public static int getUsableSimCount(Context context) {
        int i = 0;
        for (SubscriptionInfo subscriptionId : SubscriptionUtil.getAvailableSubscriptions(context)) {
            int subscriptionId2 = subscriptionId.getSubscriptionId();
            if (MotoMobileNetworkUtils.isSimInSlotUsable(context, subscriptionId2)) {
                Log.d("WifiCallingUtils", " Sim slot usable , subId: " + subscriptionId2);
                i++;
            }
        }
        return i;
    }

    public static boolean isDualSimDevice(Context context, int i) {
        TelephonyManager createForSubscriptionId = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        return createForSubscriptionId != null && createForSubscriptionId.getActiveModemCount() > 1;
    }
}

package com.motorola.settings.network;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import com.android.settings.C1992R$string;
import com.android.settings.network.telephony.MobileNetworkUtils;

public class CarrierMobileDataWarningDialogUtils {
    private static final boolean DBG = Build.TYPE.equals("userdebug");
    private static final String TAG = "CarrierMobileDataWarningDialogUtils";

    public static boolean isCarrierFtrEnabled(Context context) {
        return MotoMobileNetworkUtils.getIntentResolvedToSystemActivity(context, context.getString(C1992R$string.mobile_data_disable_dialog_intent)) != null;
    }

    public static void sendDialogIntent(Context context, int i) {
        Intent intentResolvedToSystemActivity = MotoMobileNetworkUtils.getIntentResolvedToSystemActivity(context, context.getString(C1992R$string.mobile_data_disable_dialog_intent));
        try {
            if (DBG) {
                String str = TAG;
                Log.d(str, "Start activity for: " + intentResolvedToSystemActivity.getAction());
            }
            intentResolvedToSystemActivity.setFlags(268435456);
            context.startActivity(intentResolvedToSystemActivity);
        } catch (ActivityNotFoundException unused) {
            MobileNetworkUtils.setMobileDataEnabled(context, i, false, false);
        }
    }
}

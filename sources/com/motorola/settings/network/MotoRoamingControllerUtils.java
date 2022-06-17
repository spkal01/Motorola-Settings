package com.motorola.settings.network;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import com.motorola.android.provider.MotorolaSettings;

public class MotoRoamingControllerUtils {
    public static void setDataRoamingEnabled(Context context, int i, boolean z, boolean z2) {
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            MotorolaSettings.Global.putInt(context.getContentResolver(), z ? "domestic_data_roaming" : "international_data_roaming", z2 ? 1 : 0);
            boolean dataRoamingEnabled = getDataRoamingEnabled(context, i, !z) | z2;
            TelephonyManager createForSubscriptionId = TelephonyManager.from(context).createForSubscriptionId(i);
            if (createForSubscriptionId != null && dataRoamingEnabled != createForSubscriptionId.isDataRoamingEnabled()) {
                createForSubscriptionId.setDataRoamingEnabled(dataRoamingEnabled);
            }
        }
    }

    public static boolean getDataRoamingEnabled(Context context, int i, boolean z) {
        try {
            return MotorolaSettings.Global.getInt(context.getContentResolver(), z ? "domestic_data_roaming" : "international_data_roaming") > 0;
        } catch (MotorolaSettings.SettingNotFoundException unused) {
            if (z) {
                return SubscriptionManager.getResourcesForSubId(context, i).getBoolean(17891813);
            }
            return false;
        }
    }

    public static Bundle getNationalDataRoamingPreference(Context context, int i) {
        Bundle bundle;
        CharSequence charSequence;
        CharSequence charSequence2;
        Intent intent;
        Uri parse = Uri.parse("content://com.motorola.carriersettingsext.dataroaming");
        if (context == null || i == -1) {
            return null;
        }
        try {
            bundle = context.getContentResolver().call(parse, "getDataRoamingPreferencesValues", String.valueOf(i), (Bundle) null);
        } catch (IllegalArgumentException | NullPointerException e) {
            MotoMnsLog.logd("MotoRoamingControllerUtils", "Exception National DataRoaming: " + e.getLocalizedMessage());
            bundle = null;
        }
        boolean z = false;
        if (bundle != null) {
            z = bundle.getBoolean("is_visible", false);
            intent = (Intent) bundle.getParcelable("intent");
            charSequence2 = bundle.getCharSequence("title");
            charSequence = bundle.getCharSequence("summary");
        } else {
            intent = null;
            charSequence2 = null;
            charSequence = null;
        }
        if (!z || charSequence2 == null || charSequence == null || intent == null) {
            return null;
        }
        return bundle;
    }
}

package com.android.settings.wifi.calling;

import android.content.Context;
import android.telephony.SubscriptionManager;
import com.android.settings.C1980R$bool;
import com.android.settings.SettingsActivity;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.motorola.settings.wifi.calling.WifiCallingUtils;

public class WifiCallingSuggestionActivity extends SettingsActivity {
    public static boolean isSuggestionComplete(Context context) {
        if (!context.getResources().getBoolean(C1980R$bool.config_wifi_calling_suggestion_enabled) || WifiCallingUtils.hasCustomWFCSetting(context, Integer.valueOf(SubscriptionManager.getDefaultVoiceSubscriptionId()))) {
            return true;
        }
        WifiCallingQueryImsState wifiCallingQueryImsState = new WifiCallingQueryImsState(context, SubscriptionManager.getDefaultVoiceSubscriptionId());
        if (!wifiCallingQueryImsState.isWifiCallingProvisioned()) {
            return true;
        }
        if (!wifiCallingQueryImsState.isEnabledByUser() || !wifiCallingQueryImsState.isAllowUserControl()) {
            return false;
        }
        return true;
    }
}

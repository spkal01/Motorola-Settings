package com.motorola.settings.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import com.motorola.android.provider.MotorolaSettings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TriplePowerPressEmergencyCallUtil {
    private static int TRIPLE_TAP_POWER_EMERGENCY_CALL_DISABLED = 0;
    private static int TRIPLE_TAP_POWER_EMERGENCY_CALL_ENABLED = 1;

    private static List<String> getTriplePressEmergencyCallChannelIds(Context context) {
        String[] stringArray = context.getResources().getStringArray(17236164);
        if (stringArray == null) {
            return new ArrayList();
        }
        return Arrays.asList(stringArray);
    }

    public static boolean isTriplePressEmergencyCallAvailable(Context context) {
        String channelId = getChannelId(context);
        if (TextUtils.isEmpty(channelId)) {
            return false;
        }
        return getTriplePressEmergencyCallChannelIds(context).contains(channelId);
    }

    public static boolean isTripleTapEmergencyCallEnabled(Context context) {
        return MotorolaSettings.Secure.getIntForUser(context.getContentResolver(), "pwr_triple_tap_emergency_call", isTriplePressEmergencyCallAvailable(context) ? TRIPLE_TAP_POWER_EMERGENCY_CALL_ENABLED : TRIPLE_TAP_POWER_EMERGENCY_CALL_DISABLED, -2) == TRIPLE_TAP_POWER_EMERGENCY_CALL_ENABLED;
    }

    public static boolean changeEmergencyCallEnablingState(Context context, boolean z) {
        int i;
        if (z) {
            i = TRIPLE_TAP_POWER_EMERGENCY_CALL_ENABLED;
        } else {
            i = TRIPLE_TAP_POWER_EMERGENCY_CALL_DISABLED;
        }
        return MotorolaSettings.Secure.putIntForUser(context.getContentResolver(), "pwr_triple_tap_emergency_call", i, -2);
    }

    public static String getChannelId(Context context) {
        return MotorolaSettings.Global.getString(context.getContentResolver(), "channel_id");
    }

    public static Uri getTriplePressUri() {
        return MotorolaSettings.Secure.getUriFor("pwr_triple_tap_emergency_call");
    }
}
